package com.vericode.service;

import com.vericode.model.Language;
import com.vericode.model.PRStatus;
import com.vericode.model.PullRequest;
import com.vericode.model.User;

public class PullRequestBuilder {

    private String title;
    private User author;
    private Language language;
    private String codeSnippet;
    private String description;
    private PRStatus status = PRStatus.DRAFT;

    public PullRequestBuilder title(String title) {
        this.title = title;
        return this;
    }

    public PullRequestBuilder author(User author) {
        this.author = author;
        return this;
    }

    public PullRequestBuilder language(Language language) {
        this.language = language;
        return this;
    }

    public PullRequestBuilder codeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
        return this;
    }

    public PullRequestBuilder description(String description) {
        this.description = description;
        return this;
    }

    public PullRequestBuilder status(PRStatus status) {
        this.status = status;
        return this;
    }

    public PullRequest build() {
        if (title == null || title.isBlank()) {
            throw new IllegalStateException("Title is required");
        }
        if (author == null) {
            throw new IllegalStateException("Author is required");
        }
        if (language == null) {
            throw new IllegalStateException("Language is required");
        }
        if (codeSnippet == null || codeSnippet.isBlank()) {
            throw new IllegalStateException("Code snippet is required");
        }

        return new PullRequest(title, author, language, codeSnippet, description, status);
    }
}