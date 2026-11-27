package com.aptitude.platform.service;

import com.aptitude.platform.dto.*;
import com.aptitude.platform.model.*;
import com.aptitude.platform.repository.CategoryRepository;
import com.aptitude.platform.repository.ChapterRepository;
import com.aptitude.platform.repository.QuestionOptionRepository;
import com.aptitude.platform.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PracticeService {

    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final StreakService streakService;

    public PracticeService(
            CategoryRepository categoryRepository,
            ChapterRepository chapterRepository,
            QuestionRepository questionRepository,
            QuestionOptionRepository questionOptionRepository,
            StreakService streakService
    ) {
        this.categoryRepository = categoryRepository;
        this.chapterRepository = chapterRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.streakService = streakService;
    }

    public List<CategoryDto> getAllCategoriesWithChapters() {
        return categoryRepository.findAll().stream().map(cat -> {
            List<ChapterDto> chapterDtos = cat.getChapters().stream().map(ch -> {
                long count = questionRepository.countByChapterId(ch.getId());
                return new ChapterDto(ch.getId(), ch.getTitle(), ch.getSlug(), ch.getDescription(), ch.getDisplayOrder(), count);
            }).collect(Collectors.toList());

            return new CategoryDto(cat.getId(), cat.getName(), cat.getSlug(), cat.getDescription(), cat.getIcon(), chapterDtos);
        }).collect(Collectors.toList());
    }

    public Page<QuestionDto> getQuestionsByChapter(Long chapterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questions = questionRepository.findByChapterId(chapterId, pageable);

        return questions.map(q -> {
            List<OptionDto> optionDtos = q.getOptions().stream()
                    .map(opt -> new OptionDto(opt.getId(), opt.getOptionKey(), opt.getOptionText()))
                    .collect(Collectors.toList());
            return new QuestionDto(q.getId(), q.getQuestionText(), q.getDifficulty().name(), optionDtos);
        });
    }

    @Transactional
    public CheckAnswerResponse checkAnswer(User user, CheckAnswerRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + request.getQuestionId()));

        List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderByOptionKeyAsc(question.getId());

        String correctKey = "";
        for (QuestionOption opt : options) {
            if (opt.isCorrect()) {
                correctKey = opt.getOptionKey();
                break;
            }
        }

        boolean isCorrect = correctKey.equalsIgnoreCase(request.getSelectedOption());

        // Update streak and record practice activity
        UserStreak updatedStreak = streakService.recordActivityAndCalculateStreak(user);

        return new CheckAnswerResponse(
                question.getId(),
                request.getSelectedOption(),
                correctKey,
                isCorrect,
                question.getExplanation(),
                updatedStreak.getCurrentStreak()
        );
    }
}
