package com.aptitude.platform.dto;

public class ChapterDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private int displayOrder;
    private long totalQuestions;

    public ChapterDto() {}

    public ChapterDto(Long id, String title, String slug, String description, int displayOrder, long totalQuestions) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.displayOrder = displayOrder;
        this.totalQuestions = totalQuestions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public long getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(long totalQuestions) { this.totalQuestions = totalQuestions; }
}
