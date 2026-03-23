package com.vericode.model;

public interface PRState {
    void submit(PullRequest pr);
    void approve(PullRequest pr, String reviewer);
    void requestChanges(PullRequest pr, String reviewer);
    void merge(PullRequest pr, String reviewer);
}
