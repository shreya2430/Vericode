package com.vericode.validation;

import com.vericode.model.PullRequestRequest;

import java.util.Optional;

/**
 * Chain of Responsibility pattern - abstract handler for PR validation.
 * Each concrete handler either rejects the request with an error message
 * or passes it to the next handler in the chain.
 */
public abstract class PRValidationHandler {

    private PRValidationHandler next;

    public PRValidationHandler setNext(PRValidationHandler next) {
        this.next = next;
        return next;
    }

    protected Optional<String> passToNext(PullRequestRequest request) {
        if (next != null) {
            return next.validate(request);
        }
        return Optional.empty();
    }

    public abstract Optional<String> validate(PullRequestRequest request);
}
