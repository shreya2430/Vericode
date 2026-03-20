package com.vericode.controller;

import com.vericode.model.Language;
import com.vericode.model.PullRequest;
import com.vericode.model.PRStatus;
import com.vericode.repository.PullRequestRepository;
import com.vericode.service.PullRequestBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pullrequests")
public class PRController {

    private final PullRequestRepository pullRequestRepository;

    public PRController(PullRequestRepository pullRequestRepository) {
        this.pullRequestRepository = pullRequestRepository;
    }

    // POST /api/pullrequests - Submit a new PR
    @PostMapping
    public ResponseEntity<?> createPullRequest(@RequestBody Map<String, String> request) {
        try {
            Language language = Language.valueOf(request.get("language").toUpperCase());

            PullRequest pr = new PullRequestBuilder()
                    .title(request.get("title"))
                    .author(request.get("author"))
                    .language(language)
                    .codeSnippet(request.get("codeSnippet"))
                    .description(request.get("description"))
                    .build();

            PullRequest saved = pullRequestRepository.save(pr);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

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
}