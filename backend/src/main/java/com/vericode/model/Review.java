package com.vericode.model;

/**
 * Composite Pattern: composite node that holds a collection of ReviewComponents.
 *
 * WHY THIS EXISTS:
 * A review is not a flat object — it contains multiple comments each tied to
 * a specific line of code. Review acts as the composite node that holds all
 * Comment leaves. By implementing ReviewComponent itself, it can be treated
 * the same way as a single Comment by any code that calls displayAll().
 *
 * addComment() supports CommentCommand.execute().
 * removeLastComment() supports CommentCommand.undo().
 *
 * Note: Review is not a JPA entity. It is an in-memory object created fresh
 * for each comment action and used by CommentCommand. It is not persisted
 * to the database in this implementation.
 */

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
