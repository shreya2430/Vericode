package com.vericode.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PullRequestRequest {

    private String title;
    private Long authorId;
    private String language;
    private String codeSnippet;
    private String description;

    public PullRequestRequest() {}
}