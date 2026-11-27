package com.aptitude.platform.dto;

import java.time.LocalDate;

public class HeatmapDayDto {
    private LocalDate date;
    private int count;
    private int level; // 0: 0, 1: 1-4, 2: 5-9, 3: 10-19, 4: 20+

    public HeatmapDayDto() {}

    public HeatmapDayDto(LocalDate date, int count, int level) {
        this.date = date;
        this.count = count;
        this.level = level;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}
