package com.aptitude.platform.controller;

import com.aptitude.platform.dto.HeatmapDayDto;
import com.aptitude.platform.dto.StreakSummaryDto;
import com.aptitude.platform.model.User;
import com.aptitude.platform.security.CustomUserDetails;
import com.aptitude.platform.service.StreakService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/streak")
public class StreakController {

    private final StreakService streakService;

    public StreakController(StreakService streakService) {
        this.streakService = streakService;
    }

    @GetMapping
    public ResponseEntity<StreakSummaryDto> getStreak(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(streakService.getStreakSummary(user));
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<HeatmapDayDto>> getHeatmap(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(streakService.getYearlyActivity(user));
    }
}
