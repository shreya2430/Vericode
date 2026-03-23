package com.vericode.service;

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
    public void undo() {
        pr.setStatus(previousStatus);
        pr.setState(new InReviewState());
    }

    @Override
    public String getDescription() {
        return reviewer + " requested changes on PR #" + pr.getId();
    }
}
