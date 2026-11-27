package com.aptitude.platform.controller;

import com.aptitude.platform.dto.CategoryDto;
import com.aptitude.platform.service.PracticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final PracticeService practiceService;

    public CategoryController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        return ResponseEntity.ok(practiceService.getAllCategoriesWithChapters());
    }
}
