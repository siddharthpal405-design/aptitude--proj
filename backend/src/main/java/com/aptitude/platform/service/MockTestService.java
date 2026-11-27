package com.aptitude.platform.service;

import com.aptitude.platform.dto.*;
import com.aptitude.platform.model.*;
import com.aptitude.platform.repository.MockTestRepository;
import com.aptitude.platform.repository.QuestionOptionRepository;
import com.aptitude.platform.repository.TestSessionAnswerRepository;
import com.aptitude.platform.repository.TestSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MockTestService {

    private final MockTestRepository mockTestRepository;
    private final TestSessionRepository testSessionRepository;
    private final TestSessionAnswerRepository testSessionAnswerRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final StreakService streakService;

    public MockTestService(
            MockTestRepository mockTestRepository,
            TestSessionRepository testSessionRepository,
            TestSessionAnswerRepository testSessionAnswerRepository,
            QuestionOptionRepository questionOptionRepository,
            StreakService streakService
    ) {
        this.mockTestRepository = mockTestRepository;
        this.testSessionRepository = testSessionRepository;
        this.testSessionAnswerRepository = testSessionAnswerRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.streakService = streakService;
    }

    public List<MockTestSummaryDto> getAvailableMockTests() {
        return mockTestRepository.findByIsActiveTrueOrderByCreatedAtDesc().stream()
                .map(mt -> new MockTestSummaryDto(
                        mt.getId(),
                        mt.getTitle(),
                        mt.getDescription(),
                        mt.getDurationMinutes(),
                        mt.getQuestions().size(),
                        mt.getTotalMarks(),
                        mt.getNegativeMarkingRate(),
                        mt.getDifficulty().name()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public StartTestResponseDto startTestSession(User user, UUID mockTestId) {
        MockTest mockTest = mockTestRepository.findById(mockTestId)
                .orElseThrow(() -> new IllegalArgumentException("Mock test not found with id: " + mockTestId));

        // Check for existing in-progress session
        List<TestSession> activeSessions = testSessionRepository.findByUserIdAndStatus(user.getId(), TestStatus.IN_PROGRESS);
        TestSession session = null;

        for (TestSession s : activeSessions) {
            if (s.getMockTest().getId().equals(mockTestId)) {
                // Check if time expired
                long elapsedSeconds = Duration.between(s.getStartTime(), LocalDateTime.now()).getSeconds();
                if (elapsedSeconds > (long) (mockTest.getDurationMinutes() + 1) * 60) {
                    s.setStatus(TestStatus.TIMED_OUT);
                    s.setEndTime(s.getStartTime().plusMinutes(mockTest.getDurationMinutes()));
                    testSessionRepository.save(s);
                } else {
                    session = s;
                    break;
                }
            }
        }

        if (session == null) {
            session = new TestSession(user, mockTest, mockTest.getQuestions().size());
            session = testSessionRepository.save(session);
        }

        // Map questions without isCorrect
        List<QuestionDto> questionDtos = mockTest.getQuestions().stream().map(q -> {
            List<OptionDto> optDtos = q.getOptions().stream()
                    .map(o -> new OptionDto(o.getId(), o.getOptionKey(), o.getOptionText()))
                    .collect(Collectors.toList());
            return new QuestionDto(q.getId(), q.getQuestionText(), q.getDifficulty().name(), optDtos);
        }).collect(Collectors.toList());

        // Load existing saved answers
        Map<UUID, SavedAnswerStateDto> savedAnswers = new HashMap<>();
        List<TestSessionAnswer> existingAnswers = testSessionAnswerRepository.findBySessionId(session.getId());
        for (TestSessionAnswer ans : existingAnswers) {
            savedAnswers.put(ans.getQuestion().getId(), new SavedAnswerStateDto(
                    ans.getQuestion().getId(),
                    ans.getSelectedOption(),
                    ans.isMarkedForReview(),
                    ans.getTimeSpentSeconds()
            ));
        }

        return new StartTestResponseDto(
                session.getId(),
                mockTest.getId(),
                mockTest.getTitle(),
                mockTest.getDurationMinutes(),
                mockTest.getTotalMarks(),
                mockTest.getNegativeMarkingRate(),
                session.getStartTime(),
                questionDtos,
                savedAnswers
        );
    }

    @Transactional
    public SavedAnswerStateDto saveAnswerResponse(User user, UUID sessionId, SaveAnswerRequestDto dto) {
        TestSession session = testSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found or unauthorized"));

        if (session.getStatus() != TestStatus.IN_PROGRESS) {
            throw new IllegalStateException("Test session is already finalized");
        }

        TestSessionAnswer answer = testSessionAnswerRepository.findBySessionIdAndQuestionId(sessionId, dto.getQuestionId())
                .orElseGet(() -> {
                    Question q = session.getMockTest().getQuestions().stream()
                            .filter(quest -> quest.getId().equals(dto.getQuestionId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Question not part of this mock test"));
                    return new TestSessionAnswer(session, q);
                });

        answer.setSelectedOption(dto.getSelectedOption());
        answer.setMarkedForReview(dto.isMarkedForReview());
        answer.setTimeSpentSeconds(dto.getTimeSpentSeconds());
        answer.setUpdatedAt(LocalDateTime.now());
        answer = testSessionAnswerRepository.save(answer);

        return new SavedAnswerStateDto(
                answer.getQuestion().getId(),
                answer.getSelectedOption(),
                answer.isMarkedForReview(),
                answer.getTimeSpentSeconds()
        );
    }

    @Transactional
    public TestResultResponseDto submitTest(User user, UUID sessionId) {
        TestSession session = testSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found or unauthorized"));

        if (session.getStatus() != TestStatus.IN_PROGRESS) {
            return getTestResult(user, sessionId);
        }

        MockTest test = session.getMockTest();
        session.setEndTime(LocalDateTime.now());
        session.setStatus(TestStatus.SUBMITTED);

        List<TestSessionAnswer> answers = testSessionAnswerRepository.findBySessionId(sessionId);
        Map<UUID, TestSessionAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a, (a1, a2) -> a1));

        int attempted = 0;
        int correct = 0;
        int incorrect = 0;

        for (Question q : test.getQuestions()) {
            TestSessionAnswer ans = answerMap.get(q.getId());
            if (ans != null && ans.getSelectedOption() != null && !ans.getSelectedOption().trim().isEmpty()) {
                attempted++;
                List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderByOptionKeyAsc(q.getId());
                String correctKey = options.stream()
                        .filter(QuestionOption::isCorrect)
                        .map(QuestionOption::getOptionKey)
                        .findFirst()
                        .orElse("");

                if (correctKey.equalsIgnoreCase(ans.getSelectedOption().trim())) {
                    ans.setCorrect(true);
                    correct++;
                } else {
                    ans.setCorrect(false);
                    incorrect++;
                }
                testSessionAnswerRepository.save(ans);
            }
        }

        double markPerQuestion = test.getTotalQuestions() > 0 ? ((double) test.getTotalMarks() / test.getTotalQuestions()) : 1.0;
        double rawScore = (correct * markPerQuestion) - (incorrect * test.getNegativeMarkingRate());
        double finalScore = Math.max(0.0, Math.round(rawScore * 100.0) / 100.0);
        double accuracy = attempted > 0 ? Math.round(((double) correct / attempted) * 1000.0) / 10.0 : 0.0;

        session.setAttemptedCount(attempted);
        session.setCorrectCount(correct);
        session.setIncorrectCount(incorrect);
        session.setTotalScore(finalScore);
        session.setAccuracyPercentage(accuracy);
        testSessionRepository.save(session);

        // Update User Streak & Daily Activity with attempted questions
        streakService.recordMockTestActivity(user, Math.max(1, attempted));

        return getTestResult(user, sessionId);
    }

    public TestResultResponseDto getTestResult(User user, UUID sessionId) {
        TestSession session = testSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found or unauthorized"));

        MockTest test = session.getMockTest();
        List<TestSessionAnswer> answers = testSessionAnswerRepository.findBySessionId(sessionId);
        Map<UUID, TestSessionAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a, (a1, a2) -> a1));

        List<QuestionReviewDto> reviews = test.getQuestions().stream().map(q -> {
            TestSessionAnswer ans = answerMap.get(q.getId());
            String selected = ans != null ? ans.getSelectedOption() : null;
            boolean attempted = selected != null && !selected.trim().isEmpty();

            List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderByOptionKeyAsc(q.getId());
            String correctKey = options.stream()
                    .filter(QuestionOption::isCorrect)
                    .map(QuestionOption::getOptionKey)
                    .findFirst()
                    .orElse("");

            boolean isCorrect = attempted && correctKey.equalsIgnoreCase(selected);

            List<OptionDto> optDtos = options.stream()
                    .map(o -> new OptionDto(o.getId(), o.getOptionKey(), o.getOptionText()))
                    .collect(Collectors.toList());

            return new QuestionReviewDto(
                    q.getId(),
                    q.getQuestionText(),
                    q.getDifficulty().name(),
                    selected,
                    correctKey,
                    isCorrect,
                    attempted,
                    q.getExplanation(),
                    optDtos
            );
        }).collect(Collectors.toList());

        int durationSeconds = 0;
        if (session.getEndTime() != null && session.getStartTime() != null) {
            durationSeconds = (int) Duration.between(session.getStartTime(), session.getEndTime()).getSeconds();
        }

        // Percentile formula: ((totalQuestions - incorrect) / totalQuestions) * 100 with variance
        double percentile = Math.min(99.4, Math.max(10.0, Math.round((session.getAccuracyPercentage() * 0.95 + 4.5) * 10.0) / 10.0));

        TestResultResponseDto result = new TestResultResponseDto();
        result.setSessionId(session.getId());
        result.setTestTitle(test.getTitle());
        result.setStartTime(session.getStartTime());
        result.setEndTime(session.getEndTime());
        result.setTotalQuestions(session.getTotalQuestions());
        result.setAttemptedCount(session.getAttemptedCount());
        result.setCorrectCount(session.getCorrectCount());
        result.setIncorrectCount(session.getIncorrectCount());
        result.setSkippedCount(session.getTotalQuestions() - session.getAttemptedCount());
        result.setTotalScore(session.getTotalScore());
        result.setMaxMarks(test.getTotalMarks());
        result.setAccuracyPercentage(session.getAccuracyPercentage());
        result.setPercentileEstimate(percentile);
        result.setTotalTimeTakenSeconds(durationSeconds);
        result.setReviewList(reviews);

        return result;
    }
}
