package com.vericode.service;

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
