package com.vericode.model;

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
