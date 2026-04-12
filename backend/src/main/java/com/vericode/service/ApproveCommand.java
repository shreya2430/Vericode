package com.vericode.service;

/**
 * Command Pattern: encapsulates the approve action as a reversible object.
 *
 * WHY THIS EXISTS:
 * When a reviewer approves a PR, the previous status needs to be recoverable
 * in case the reviewer made a mistake. This class snapshots previousStatus
 * before calling the State pattern's approve() method. If undo() is called,
 * the PR is restored to its previous status and state object.
 *
 * EXECUTE: snapshots current status → calls pr.getState().approve(pr, reviewer)
 * UNDO:    restores previousStatus → resets state to InReviewState
 */

import com.vericode.model.PRStatus;
import com.vericode.model.PullRequest;
import com.vericode.model.InReviewState;

public class ApproveCommand implements ReviewCommand{
    private final PullRequest pr;
    private final String reviewer;
    private PRStatus previousStatus;

    public ApproveCommand(PullRequest pr, String reviewer) {
        this.pr = pr;
        this.reviewer = reviewer;
    }

    @Override
    public void execute() {
        previousStatus = pr.getStatus();
        pr.getState().approve(pr, reviewer);
    }

    @Override
    public void undo() {
        pr.setStatus(previousStatus);
        pr.setState(new InReviewState());
    }

    @Override
    public String getDescription() {
        return reviewer + " approved PR #" + pr.getId() + " by " + pr.getAuthor().getUsername();
    }
}
