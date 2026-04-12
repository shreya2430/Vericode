package com.vericode.service;

/**
 * Command Pattern: encapsulates the comment action as a reversible object.
 *
 * WHY THIS EXISTS:
 * Comments are added to a Review object at a specific line number. Making this
 * a Command means comments can be undone just like state transitions. On execute()
 * a new Comment is added to the Review. On undo() the last comment is removed.
 * This keeps the undo stack consistent — every action, including commenting, is
 * reversible through the same mechanism.
 *
 * EXECUTE: creates a new Comment and adds it to the Review via addComment()
 * UNDO:    removes the last comment added via removeLastComment()
 *
 * Note: commenting does not change PR status so notifyAll is intentionally
 * skipped. The Facade's comment() method makes this explicit.
 */



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
