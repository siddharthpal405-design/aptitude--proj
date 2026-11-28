package com.aptitude.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chapters")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;

    @Column(length = 1000)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String studyMaterialJson;

    private int displayOrder = 0;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Question> questions = new ArrayList<>();

    public Chapter() {}

    public Chapter(Category category, String title, String slug, String description, int displayOrder) {
        this.category = category;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStudyMaterialJson() { return studyMaterialJson; }
    public void setStudyMaterialJson(String studyMaterialJson) { this.studyMaterialJson = studyMaterialJson; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
