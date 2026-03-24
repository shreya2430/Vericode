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

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "`language`", nullable = false)
    private Language language;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String codeSnippet;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "`status`", nullable = false)
    private PRStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private PRState state;

    // Default constructor (required by JPA)
    public PullRequest() {}

    // Constructor used by Builder
    public PullRequest(String title, User author, Language language,
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
        this.state = new DraftState();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}