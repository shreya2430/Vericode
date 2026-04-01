package com.vericode.validation;

import com.vericode.model.PullRequestRequest;

import java.util.Optional;

public class TitleValidationHandler extends PRValidationHandler {

    private static final int MAX_TITLE_LENGTH = 100;

    @Override
    public Optional<String> validate(PullRequestRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return Optional.of("PR title must not be empty");
        }
        if (request.getTitle().length() > MAX_TITLE_LENGTH) {
            return Optional.of("PR title must not exceed " + MAX_TITLE_LENGTH + " characters");
        }
        return passToNext(request);
    }
}
