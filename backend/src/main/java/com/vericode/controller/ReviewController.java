package com.vericode.controller;

import com.vericode.service.ReviewActionResult;
import com.vericode.service.ReviewFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Facade Pattern: thin HTTP adapter for review lifecycle actions.
 *
 * This controller's only responsibilities are:
 *   1. Parse the incoming HTTP request (path variables, request body)
 *   2. Validate that required fields are present
 *   3. Delegate to ReviewFacade
 *   4. Map the result (or exception) to an HTTP response
 *
 * All business logic — loading PRs, executing commands, saving, notifying —
 * lives in ReviewFacade. This class has no direct dependency on the repository,
 * command history, or notification service.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewFacade facade;

    public ReviewController(ReviewFacade facade) {
        this.facade = facade;
    }

    // POST /api/reviews/{prId}/submit
    @PostMapping("/{prId}/submit")
    public ResponseEntity<?> submit(@PathVariable Long prId) {
        try {
            var pr = facade.submit(prId);
            return ResponseEntity.ok(Map.of("status", pr.getStatus()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/reviews/{prId}/approve
    // Body: { "reviewer": "keya" }
    @PostMapping("/{prId}/approve")
    public ResponseEntity<?> approve(@PathVariable Long prId,
                                     @RequestBody Map<String, String> body) {
        try {
            String reviewer = body.get("reviewer");
            if (reviewer == null || reviewer.isBlank())
                return ResponseEntity.badRequest().body(Map.of("error", "Reviewer name is required"));

            ReviewActionResult result = facade.approve(prId, reviewer);
            return ResponseEntity.ok(Map.of("status", result.status(), "message", result.message()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/reviews/{prId}/request-changes
    // Body: { "reviewer": "keya" }
    @PostMapping("/{prId}/request-changes")
    public ResponseEntity<?> requestChanges(@PathVariable Long prId,
                                            @RequestBody Map<String, String> body) {
        try {
            String reviewer = body.get("reviewer");
            if (reviewer == null || reviewer.isBlank())
                return ResponseEntity.badRequest().body(Map.of("error", "Reviewer name is required"));

            ReviewActionResult result = facade.requestChanges(prId, reviewer);
            return ResponseEntity.ok(Map.of("status", result.status(), "message", result.message()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/reviews/{prId}/merge
    // Body: { "reviewer": "keya" }
    @PostMapping("/{prId}/merge")
    public ResponseEntity<?> merge(@PathVariable Long prId,
                                   @RequestBody Map<String, String> body) {
        try {
            String reviewer = body.get("reviewer");
            if (reviewer == null || reviewer.isBlank())
                return ResponseEntity.badRequest().body(Map.of("error", "Reviewer name is required"));

            ReviewActionResult result = facade.merge(prId, reviewer);
            return ResponseEntity.ok(Map.of("status", result.status(), "message", result.message()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/reviews/{prId}/comment
    // Body: { "author": "keya", "lineNumber": "10", "content": "Fix this" }
    @PostMapping("/{prId}/comment")
    public ResponseEntity<?> comment(@PathVariable Long prId,
                                     @RequestBody Map<String, String> body) {
        try {
            String author = body.get("author");
            String content = body.get("content");
            String lineNumberStr = body.get("lineNumber");

            if (author == null || author.isBlank())
                return ResponseEntity.badRequest().body(Map.of("error", "Author is required"));
            if (content == null || content.isBlank())
                return ResponseEntity.badRequest().body(Map.of("error", "Comment content is required"));
            if (lineNumberStr == null)
                return ResponseEntity.badRequest().body(Map.of("error", "lineNumber is required"));

            int lineNumber = Integer.parseInt(lineNumberStr);
            String description = facade.comment(prId, author, lineNumber, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", description));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "lineNumber must be a number"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/reviews/undo
    @PostMapping("/undo")
    public ResponseEntity<?> undo() {
        try {
            facade.undo();
            return ResponseEntity.ok(Map.of("message", "Last action undone"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/reviews/history
    @GetMapping("/history")
    public ResponseEntity<List<String>> history() {
        return ResponseEntity.ok(facade.getHistory());
    }
}
