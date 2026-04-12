package com.vericode.model;

/**
 * Composite Pattern: leaf node representing a single line-level code comment.
 *
 * WHY THIS EXISTS:
 * A Comment is the smallest unit in the review tree. It holds the line number,
 * content, and author of a single piece of feedback. As a leaf node it has no
 * children and displayAll() prints only its own data. It implements
 * ReviewComponent so it can be stored in Review's children list alongside
 * any other ReviewComponent without the Review needing to know the difference.
 */

// Composite Leaf: Represents an individual comment in the review

public class Comment implements ReviewComponent{
    private int lineNumber;
    private String content;
    private String author;

    public Comment(int lineNumber, String content, String author) {
        this.lineNumber = lineNumber;
        this.content = content;
        this.author = author;
    }
    @Override
    public void display() {
        System.out.println("  Line " + lineNumber
                + " [" + author + "]: " + content);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }


}
