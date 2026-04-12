package com.vericode.model;

/**
 * State Pattern: defines the contract for all PR lifecycle states.
 *
 * WHY THIS EXISTS:
 * Without the State pattern, every action (approve, merge, etc.) would need
 * a long if/else block checking pr.getStatus() before doing anything. That
 * logic would be scattered across controllers and services. Instead, each
 * state class encapsulates exactly what is allowed from that stage.
 *
 * HOW IT WORKS:
 * PullRequest holds a reference to its current PRState object. Every lifecycle
 * method call is delegated to that object. Valid transitions update the status
 * and swap the state reference. Invalid transitions throw IllegalStateException
 * immediately with a descriptive message.
 *
 * VALID FLOW:
 *   DRAFT → IN_REVIEW → APPROVED → MERGED
 *                    ↘ CHANGES_REQUESTED → IN_REVIEW (loop back)
 *
 * Note: submit() does not take a reviewer because submitting is done by the
 * author. approve(), requestChanges(), and merge() take a reviewer because
 * those actions are performed by someone other than the author.
 */



public interface PRState {
    void submit(PullRequest pr);
    void approve(PullRequest pr, String reviewer);
    void requestChanges(PullRequest pr, String reviewer);
    void merge(PullRequest pr, String reviewer);
}
