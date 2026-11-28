package com.aptitude.platform.dto;

public class ChapterDetailDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private long totalQuestions;
    private long easyCount;
    private long mediumCount;
    private long hardCount;
    private StudyMaterialDto studyMaterial;

    public ChapterDetailDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategorySlug() { return categorySlug; }
    public void setCategorySlug(String categorySlug) { this.categorySlug = categorySlug; }

    public long getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(long totalQuestions) { this.totalQuestions = totalQuestions; }

    public long getEasyCount() { return easyCount; }
    public void setEasyCount(long easyCount) { this.easyCount = easyCount; }

    public long getMediumCount() { return mediumCount; }
    public void setMediumCount(long mediumCount) { this.mediumCount = mediumCount; }

    public long getHardCount() { return hardCount; }
    public void setHardCount(long hardCount) { this.hardCount = hardCount; }

    public StudyMaterialDto getStudyMaterial() { return studyMaterial; }
    public void setStudyMaterial(StudyMaterialDto studyMaterial) { this.studyMaterial = studyMaterial; }
}
