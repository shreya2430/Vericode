package com.vericode.service;

/**
 * Command Pattern: encapsulates the request-changes action as a reversible object.
 *
 * WHY THIS EXISTS:
 * Requesting changes is treated as a reviewer action that can be undone, just
 * like approval. The reviewer may have clicked the wrong button or changed their
 * mind. This class snapshots previousStatus before calling requestChanges() so
 * the PR can be fully restored if undo() is called.
 *
 * EXECUTE: snapshots current status → calls pr.getState().requestChanges(pr, reviewer)
 * UNDO:    restores previousStatus → resets state to InReviewState
 */

import com.vericode.model.PRStatus;
import com.vericode.model.PullRequest;
import com.vericode.model.InReviewState;

public class RejectCommand implements ReviewCommand{
    private final PullRequest pr;
    private final String reviewer;
    private PRStatus previousStatus;

    public RejectCommand(PullRequest pr, String reviewer) {
        this.pr = pr;
        this.reviewer = reviewer;
    }

    @Override
    public void execute() {
        previousStatus = pr.getStatus();
        pr.getState().requestChanges(pr, reviewer);  // reviewer passed here now
    }

    @Override
    public PullRequest undo() {
        pr.setStatus(previousStatus);
        pr.setState(new InReviewState());
        return pr;
    }

    @Override
    public String getDescription() {
        return reviewer + " requested changes on PR #" + pr.getId()
                + " by " + pr.getAuthor().getUsername();
    }
}
