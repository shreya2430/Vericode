package com.vericode.controller;

import com.vericode.checker.CheckResult;
import com.vericode.checker.CheckerFactory;
import com.vericode.checker.CodeChecker;
import com.vericode.model.Language;
import com.vericode.model.PullRequest;
import com.vericode.model.PullRequestRequest;
import com.vericode.model.ReviewTemplate;
import com.vericode.model.User;
import com.vericode.repository.PullRequestRepository;
import com.vericode.repository.UserRepository;
import com.vericode.service.PullRequestBuilder;
import com.vericode.service.ReviewTemplateRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pullrequests")
public class PRController {

    private final PullRequestRepository pullRequestRepository;
    private final UserRepository userRepository;

    public PRController(PullRequestRepository pullRequestRepository,
                        UserRepository userRepository) {
        this.pullRequestRepository = pullRequestRepository;
        this.userRepository = userRepository;
    }

    // POST /api/pullrequests - Submit a new PR
    @PostMapping
    public ResponseEntity<?> createPullRequest(@RequestBody PullRequestRequest request) {
        try {
            // Look up the author
            Optional<User> authorOpt = userRepository.findById(request.getAuthorId());
            if (authorOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Author not found with id: " + request.getAuthorId()));
            }

            Language language = Language.valueOf(request.getLanguage().toUpperCase());

            // Builder pattern
            PullRequest pr = new PullRequestBuilder()
                    .title(request.getTitle())
                    .author(authorOpt.get())
                    .language(language)
                    .codeSnippet(request.getCodeSnippet())
                    .description(request.getDescription())
                    .build();

            PullRequest saved = pullRequestRepository.save(pr);

            // Factory pattern
            CodeChecker checker = CheckerFactory.createChecker(language);
            CheckResult checkResult = checker.check(request.getCodeSnippet());

            // Prototype pattern
            ReviewTemplate template = ReviewTemplateRegistry.getTemplate("STANDARD");

            Map<String, Object> response = new HashMap<>();
            response.put("pullRequest", saved);
            response.put("checkResult", Map.of(
                    "passed", checkResult.isPassed(),
                    "violations", checkResult.getViolations(),
                    "errorCount", checkResult.getErrorCount(),
                    "warningCount", checkResult.getWarningCount()
            ));
            response.put("reviewChecklist", template.getChecklistItems());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid language. Supported: JAVA, PYTHON, JAVASCRIPT"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/pullrequests - Get all PRs
    @GetMapping
    public ResponseEntity<List<PullRequest>> getAllPullRequests() {
        return ResponseEntity.ok(pullRequestRepository.findAll());
    }

    // GET /api/pullrequests/{id} - Get PR by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPullRequestById(@PathVariable Long id) {
        return pullRequestRepository.findById(id)
                .map(pr -> ResponseEntity.ok((Object) pr))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Pull request not found with id: " + id)));
    }

    // PUT /api/pullrequests/{id} - Update a PR
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePullRequest(@PathVariable Long id,
                                               @RequestBody PullRequestRequest request) {
        return pullRequestRepository.findById(id)
                .map(existingPr -> {
                    try {
                        if (request.getTitle() != null) existingPr.setTitle(request.getTitle());
                        if (request.getDescription() != null) existingPr.setDescription(request.getDescription());
                        if (request.getCodeSnippet() != null) existingPr.setCodeSnippet(request.getCodeSnippet());
                        if (request.getLanguage() != null) {
                            existingPr.setLanguage(Language.valueOf(request.getLanguage().toUpperCase()));
                        }

                        PullRequest updated = pullRequestRepository.save(existingPr);
                        return ResponseEntity.ok((Object) updated);

                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                .body((Object) Map.of("error", "Invalid language. Supported: JAVA, PYTHON, JAVASCRIPT"));
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Pull request not found with id: " + id)));
    }

    // DELETE /api/pullrequests/{id} - Delete a PR
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePullRequest(@PathVariable Long id) {
        return pullRequestRepository.findById(id)
                .map(pr -> {
                    pullRequestRepository.delete(pr);
                    return ResponseEntity.ok((Object) Map.of("message", "Pull request deleted successfully"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Pull request not found with id: " + id)));
    }
}