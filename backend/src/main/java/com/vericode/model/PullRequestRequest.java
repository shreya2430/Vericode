package com.vericode.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PullRequestRequest {

    private String title;
    private String author;
    private String language;
    private String codeSnippet;
    private String description;

    // Default constructor (needed for Jackson deserialization)
    public PullRequestRequest() {}
}