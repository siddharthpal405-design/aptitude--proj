package com.aptitude.platform.controller;

import com.aptitude.platform.dto.*;
import com.aptitude.platform.model.User;
import com.aptitude.platform.security.CustomUserDetails;
import com.aptitude.platform.service.MockTestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mock-tests")
public class MockTestController {

    private final MockTestService mockTestService;

    public MockTestController(MockTestService mockTestService) {
        this.mockTestService = mockTestService;
    }

    @GetMapping
    public ResponseEntity<List<MockTestSummaryDto>> getMockTests() {
        return ResponseEntity.ok(mockTestService.getAvailableMockTests());
    }

    @PostMapping("/{testId}/start")
    public ResponseEntity<StartTestResponseDto> startTest(
            @PathVariable UUID testId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(mockTestService.startTestSession(user, testId));
    }

    @PostMapping("/sessions/{sessionId}/save-response")
    public ResponseEntity<SavedAnswerStateDto> saveAnswer(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SaveAnswerRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(mockTestService.saveAnswerResponse(user, sessionId, dto));
    }

    @PostMapping("/sessions/{sessionId}/submit")
    public ResponseEntity<TestResultResponseDto> submitTest(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(mockTestService.submitTest(user, sessionId));
    }

    @GetMapping("/sessions/{sessionId}/result")
    public ResponseEntity<TestResultResponseDto> getResult(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(mockTestService.getTestResult(user, sessionId));
    }
}
