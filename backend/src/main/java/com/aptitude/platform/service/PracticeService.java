package com.aptitude.platform.service;

import com.aptitude.platform.dto.*;
import com.aptitude.platform.model.*;
import com.aptitude.platform.repository.CategoryRepository;
import com.aptitude.platform.repository.ChapterRepository;
import com.aptitude.platform.repository.QuestionOptionRepository;
import com.aptitude.platform.repository.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PracticeService {

    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final StreakService streakService;
    private final ObjectMapper objectMapper;

    public PracticeService(
            CategoryRepository categoryRepository,
            ChapterRepository chapterRepository,
            QuestionRepository questionRepository,
            QuestionOptionRepository questionOptionRepository,
            StreakService streakService,
            ObjectMapper objectMapper
    ) {
        this.categoryRepository = categoryRepository;
        this.chapterRepository = chapterRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.streakService = streakService;
        this.objectMapper = objectMapper;
    }

    public List<CategoryDto> getAllCategoriesWithChapters() {
        return categoryRepository.findAllByOrderByIdAsc().stream().map(cat -> {
            List<ChapterDto> chapterDtos = cat.getChapters().stream().map(ch -> {
                long count = questionRepository.countByChapterId(ch.getId());
                return new ChapterDto(ch.getId(), ch.getTitle(), ch.getSlug(), ch.getDescription(), ch.getDisplayOrder(), count);
            }).collect(Collectors.toList());

            return new CategoryDto(cat.getId(), cat.getName(), cat.getSlug(), cat.getDescription(), cat.getIcon(), chapterDtos);
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChapterDetailDto> getAllShortNotes() {
        List<ChapterDetailDto> list = new ArrayList<>();
        for (Category cat : categoryRepository.findAllByOrderByIdAsc()) {
            for (Chapter ch : cat.getChapters()) {
                list.add(buildChapterDetailDto(ch, cat));
            }
        }
        return list;
    }

    @Transactional(readOnly = true)
    public ChapterDetailDto getChapterDetails(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + chapterId));
        return buildChapterDetailDto(chapter, chapter.getCategory());
    }

    private ChapterDetailDto buildChapterDetailDto(Chapter chapter, Category category) {
        ChapterDetailDto dto = new ChapterDetailDto();
        dto.setId(chapter.getId());
        dto.setTitle(chapter.getTitle());
        dto.setSlug(chapter.getSlug());
        dto.setDescription(chapter.getDescription());
        dto.setCategoryId(category.getId());
        dto.setCategoryName(category.getName());
        dto.setCategorySlug(category.getSlug());
        dto.setTotalQuestions(questionRepository.countByChapterId(chapter.getId()));
        dto.setEasyCount(questionRepository.countByChapterIdAndDifficulty(chapter.getId(), Difficulty.EASY));
        dto.setMediumCount(questionRepository.countByChapterIdAndDifficulty(chapter.getId(), Difficulty.MEDIUM));
        dto.setHardCount(questionRepository.countByChapterIdAndDifficulty(chapter.getId(), Difficulty.HARD));

        if (chapter.getStudyMaterialJson() != null && !chapter.getStudyMaterialJson().isBlank()) {
            try {
                StudyMaterialDto material = objectMapper.readValue(chapter.getStudyMaterialJson(), StudyMaterialDto.class);
                dto.setStudyMaterial(material);
            } catch (Exception e) {
                dto.setStudyMaterial(new StudyMaterialDto(chapter.getDescription(), List.of(), List.of(), null));
            }
        } else {
            dto.setStudyMaterial(new StudyMaterialDto(chapter.getDescription(), List.of(), List.of(), null));
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<QuestionDto> getQuestionsByChapter(Long chapterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questions = questionRepository.searchByChapter(chapterId, null, null, pageable);

        return questions.map(q -> {
            List<OptionDto> optionDtos = q.getOptions().stream()
                    .map(opt -> new OptionDto(opt.getId(), opt.getOptionKey(), opt.getOptionText()))
                    .collect(Collectors.toList());
            return new QuestionDto(q.getId(), q.getQuestionText(), q.getDifficulty().name(), optionDtos);
        });
    }

    @Transactional(readOnly = true)
    public Page<QuestionListItemDto> getChapterQuestionList(Long chapterId, String difficultyStr, String search, int page, int size) {
        Difficulty difficulty = null;
        if (difficultyStr != null && !difficultyStr.isBlank() && !difficultyStr.equalsIgnoreCase("ALL")) {
            try {
                difficulty = Difficulty.valueOf(difficultyStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        String normalizedSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questions = questionRepository.searchByChapter(chapterId, difficulty, normalizedSearch, pageable);

        return questions.map(q -> {
            List<OptionDto> optionDtos = q.getOptions().stream()
                    .map(opt -> new OptionDto(opt.getId(), opt.getOptionKey(), opt.getOptionText()))
                    .collect(Collectors.toList());
            String correctKey = q.getOptions().stream()
                    .filter(QuestionOption::isCorrect)
                    .map(QuestionOption::getOptionKey)
                    .findFirst()
                    .orElse("");
            return new QuestionListItemDto(
                    q.getId(),
                    q.getQuestionText(),
                    q.getDifficulty().name(),
                    optionDtos,
                    correctKey,
                    q.getExplanation()
            );
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
