package com.aptitude.platform.controller;

import com.aptitude.platform.dto.*;
import com.aptitude.platform.model.User;
import com.aptitude.platform.security.CustomUserDetails;
import com.aptitude.platform.service.PracticeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practice")
public class PracticeController {

    private final PracticeService practiceService;

    public PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @GetMapping("/short-notes")
    public ResponseEntity<List<ChapterDetailDto>> getAllShortNotes() {
        return ResponseEntity.ok(practiceService.getAllShortNotes());
    }

    @GetMapping("/chapters/{chapterId}/details")
    public ResponseEntity<ChapterDetailDto> getChapterDetails(@PathVariable Long chapterId) {
        return ResponseEntity.ok(practiceService.getChapterDetails(chapterId));
    }

    @GetMapping("/chapters/{chapterId}/questions")
    public ResponseEntity<Page<QuestionDto>> getQuestions(
            @PathVariable Long chapterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(practiceService.getQuestionsByChapter(chapterId, page, size));
    }

    @GetMapping("/chapters/{chapterId}/question-list")
    public ResponseEntity<Page<QuestionListItemDto>> getQuestionList(
            @PathVariable Long chapterId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(practiceService.getChapterQuestionList(chapterId, difficulty, search, page, size));
    }

    @PostMapping("/check-answer")
    public ResponseEntity<CheckAnswerResponse> checkAnswer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CheckAnswerRequest request
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(practiceService.checkAnswer(user, request));
    }
}
