package com.aptitude.platform.service;

import com.aptitude.platform.dto.BulkUploadResultDto;
import com.aptitude.platform.model.*;
import com.aptitude.platform.repository.ChapterRepository;
import com.aptitude.platform.repository.MockTestRepository;
import com.aptitude.platform.repository.QuestionOptionRepository;
import com.aptitude.platform.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminService {

    private final ChapterRepository chapterRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final MockTestRepository mockTestRepository;

    public AdminService(
            ChapterRepository chapterRepository,
            QuestionRepository questionRepository,
            QuestionOptionRepository questionOptionRepository,
            MockTestRepository mockTestRepository
    ) {
        this.chapterRepository = chapterRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.mockTestRepository = mockTestRepository;
    }

    @Transactional
    public BulkUploadResultDto bulkUploadQuestionsFromCsv(String csvContent) {
        BulkUploadResultDto result = new BulkUploadResultDto();
        if (csvContent == null || csvContent.trim().isEmpty()) {
            result.getErrors().add("CSV content is empty");
            return result;
        }

        String[] lines = csvContent.split("\r?\n");
        int processed = 0;
        int added = 0;
        int skipped = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            // Skip header line
            if (i == 0 && line.toLowerCase().startsWith("chapter_slug")) {
                continue;
            }

            processed++;
            List<String> tokens = parseCsvLine(line);
            if (tokens.size() < 9) {
                skipped++;
                result.getErrors().add("Line " + (i + 1) + ": Insufficient columns (expected 9, got " + tokens.size() + ")");
                continue;
            }

            String chapterSlug = tokens.get(0).trim();
            String questionText = tokens.get(1).trim();
            String diffStr = tokens.get(2).trim();
            String optA = tokens.get(3).trim();
            String optB = tokens.get(4).trim();
            String optC = tokens.get(5).trim();
            String optD = tokens.get(6).trim();
            String correctKey = tokens.get(7).trim().toUpperCase();
            String explanation = tokens.get(8).trim();

            Optional<Chapter> chapterOpt = chapterRepository.findBySlug(chapterSlug);
            if (chapterOpt.isEmpty()) {
                skipped++;
                result.getErrors().add("Line " + (i + 1) + ": Chapter slug not found: " + chapterSlug);
                continue;
            }

            Difficulty difficulty = Difficulty.MEDIUM;
            try {
                difficulty = Difficulty.valueOf(diffStr.toUpperCase());
            } catch (Exception ignored) {}

            Question question = new Question(chapterOpt.get(), questionText, difficulty, explanation);
            question = questionRepository.save(question);

            String[] options = { optA, optB, optC, optD };
            String[] keys = { "A", "B", "C", "D" };

            for (int k = 0; k < 4; k++) {
                boolean isCorrect = keys[k].equalsIgnoreCase(correctKey);
                QuestionOption option = new QuestionOption(keys[k], options[k], isCorrect);
                question.addOption(option);
            }
            questionRepository.save(question);
            added++;
        }

        result.setTotalProcessed(processed);
        result.setSuccessfullyAdded(added);
        result.setSkippedCount(skipped);
        return result;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    current.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result;
    }

    @Transactional
    public MockTest createMockTestWithAllQuestions(String title, String description, int durationMinutes, int marks, double negativeMarking) {
        List<Question> allQuestions = questionRepository.findAll();
        Collections.shuffle(allQuestions);

        int sampleSize = Math.min(allQuestions.size(), 15);
        List<Question> selected = allQuestions.subList(0, sampleSize);

        MockTest mockTest = new MockTest(title, description, durationMinutes, marks, negativeMarking, Difficulty.MEDIUM);
        mockTest.setQuestions(selected);
        return mockTestRepository.save(mockTest);
    }
}
