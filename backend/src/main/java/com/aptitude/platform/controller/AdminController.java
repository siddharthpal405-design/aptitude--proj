package com.aptitude.platform.controller;

import com.aptitude.platform.dto.BulkUploadResultDto;
import com.aptitude.platform.model.MockTest;
import com.aptitude.platform.service.AdminService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(value = "/questions/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BulkUploadResultDto> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        try {
            String content = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            return ResponseEntity.ok(adminService.bulkUploadQuestionsFromCsv(content));
        } catch (Exception e) {
            BulkUploadResultDto error = new BulkUploadResultDto();
            error.getErrors().add("Failed to process file: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping(value = "/questions/bulk-upload-raw", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<BulkUploadResultDto> uploadRawCsv(@RequestBody String csvContent) {
        return ResponseEntity.ok(adminService.bulkUploadQuestionsFromCsv(csvContent));
    }

    @PostMapping("/mock-tests/generate")
    public ResponseEntity<Map<String, Object>> generateMockTest(
            @RequestParam(defaultValue = "Comprehensive Aptitude Mock Test") String title,
            @RequestParam(defaultValue = "Complete exam simulator covering Quantitative, Logical Reasoning, and Verbal Ability.") String description,
            @RequestParam(defaultValue = "30") int durationMinutes,
            @RequestParam(defaultValue = "15") int marks,
            @RequestParam(defaultValue = "0.25") double negativeMarkingRate
    ) {
        MockTest test = adminService.createMockTestWithAllQuestions(title, description, durationMinutes, marks, negativeMarkingRate);
        return ResponseEntity.ok(Map.of(
                "id", test.getId(),
                "title", test.getTitle(),
                "totalQuestions", test.getTotalQuestions(),
                "durationMinutes", test.getDurationMinutes()
        ));
    }
}
