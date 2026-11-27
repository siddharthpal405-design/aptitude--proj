package com.aptitude.platform.dto;

import java.util.List;

public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private List<ChapterDto> chapters;

    public CategoryDto() {}

    public CategoryDto(Long id, String name, String slug, String description, String icon, List<ChapterDto> chapters) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.icon = icon;
        this.chapters = chapters;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public List<ChapterDto> getChapters() { return chapters; }
    public void setChapters(List<ChapterDto> chapters) { this.chapters = chapters; }
}
