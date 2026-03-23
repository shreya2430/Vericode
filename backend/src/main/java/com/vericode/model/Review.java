package com.vericode.model;

import java.util.ArrayList;
import java.util.List;

// Composite Node representing a review with comments

public class Review implements ReviewComponent{
    private String reviewerName;
    private List<ReviewComponent> comments = new ArrayList<>();

    public Review(String reviewerName) {
        this.reviewerName = reviewerName;
    }
    public void addComment(ReviewComponent c) {
        comments.add(c);
    }
    public void removeLastComment() {
        if (!comments.isEmpty())
            comments.remove(comments.size() - 1);
    }
    @Override
    public void display() {
        System.out.println("Review by: " + reviewerName);
        comments.forEach(ReviewComponent::display);
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public List<ReviewComponent> getComments() {
        return comments;
    }
}
