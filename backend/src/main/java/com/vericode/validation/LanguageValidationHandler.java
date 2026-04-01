package com.vericode.validation;

import com.vericode.model.Language;
import com.vericode.model.PullRequestRequest;

import java.util.Optional;

public class LanguageValidationHandler extends PRValidationHandler {

    @Override
    public Optional<String> validate(PullRequestRequest request) {
        if (request.getLanguage() == null || request.getLanguage().isBlank()) {
            return Optional.of("Language must not be empty");
        }
        try {
            Language.valueOf(request.getLanguage().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Optional.of("Invalid language. Supported: JAVA, PYTHON, JAVASCRIPT");
        }
        return passToNext(request);
    }
}
