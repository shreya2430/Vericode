package com.vericode.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pull_requests")
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String codeSnippet;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PRStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor (required by JPA)
    public PullRequest() {}

    // Constructor used by Builder
    public PullRequest(String title, String author, Language language,
                       String codeSnippet, String description, PRStatus status) {
        this.title = title;
        this.author = author;
        this.language = language;
        this.codeSnippet = codeSnippet;
        this.description = description;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PRStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}