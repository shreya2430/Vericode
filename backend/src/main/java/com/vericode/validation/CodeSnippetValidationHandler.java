package com.vericode.validation;

import com.vericode.model.PullRequestRequest;

import java.util.Optional;

public class CodeSnippetValidationHandler extends PRValidationHandler {

    private static final int MAX_CODE_LENGTH = 5000;

    @Override
    public Optional<String> validate(PullRequestRequest request) {
        if (request.getCodeSnippet() == null || request.getCodeSnippet().isBlank()) {
            return Optional.of("Code snippet must not be empty");
        }
        if (request.getCodeSnippet().length() > MAX_CODE_LENGTH) {
            return Optional.of("Code snippet must not exceed " + MAX_CODE_LENGTH + " characters");
        }
        return passToNext(request);
    }
}
