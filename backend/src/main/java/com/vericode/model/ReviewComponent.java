package com.vericode.model;

/**
 * Composite Pattern: common interface for both Review nodes and Comment leaves.
 *
 * WHY THIS EXISTS:
 * Without this interface, any code that wants to display or process a review
 * would need to know about its internal structure — iterate the comment list,
 * call display on each one separately. With this interface, both Review and
 * Comment are treated uniformly. Calling displayAll() on a Review cascades
 * automatically to every Comment inside it without any external iteration.
 */

// Composite Interface for Review and Comment
public interface ReviewComponent {
    void display();
}
