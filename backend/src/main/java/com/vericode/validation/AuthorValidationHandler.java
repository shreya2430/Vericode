package com.vericode.validation;

import com.vericode.model.PullRequestRequest;
import com.vericode.repository.UserRepository;

import java.util.Optional;

public class AuthorValidationHandler extends PRValidationHandler {

    private final UserRepository userRepository;

    public AuthorValidationHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<String> validate(PullRequestRequest request) {
        if (request.getAuthorId() == null) {
            return Optional.of("Author ID must not be null");
        }
        if (!userRepository.existsById(request.getAuthorId())) {
            return Optional.of("Author not found with id: " + request.getAuthorId());
        }
        return passToNext(request);
    }
}
