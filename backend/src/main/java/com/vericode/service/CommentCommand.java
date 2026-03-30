package com.vericode.service;

import com.vericode.model.Comment;
import com.vericode.model.PullRequest;
import com.vericode.model.Review;

public class CommentCommand implements ReviewCommand{
    private final PullRequest pr;
    private final Review review;
    private final String author;
    private final int lineNumber;
    private final String content;

    public CommentCommand(PullRequest pr, Review review,
                          String author, int lineNumber, String content) {
        this.pr = pr;
        this.review = review;
        this.author = author;
        this.lineNumber = lineNumber;
        this.content = content;
    }

    @Override
    public void execute() {
        review.addComment(new Comment(lineNumber, content, author));
    }

    @Override
    public void undo() {
        review.removeLastComment();
    }

    @Override
    public String getDescription() {
        return author + " commented on PR #" + pr.getId()
                + " (author: " + pr.getAuthor().getUsername() + ")"  
                + " at line " + lineNumber + ": " + content;
    }
}
