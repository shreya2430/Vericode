package com.vericode.controller;

import com.vericode.model.PullRequest;
import com.vericode.model.Review;
import com.vericode.repository.PullRequestRepository;
import com.vericode.service.ApproveCommand;
import com.vericode.service.CommandHistory;
import com.vericode.service.CommentCommand;
import com.vericode.service.RejectCommand;
import com.vericode.service.ReviewCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final PullRequestRepository prRepo;
    private final CommandHistory commandHistory;

    public ReviewController(PullRequestRepository prRepo,
                            CommandHistory commandHistory) {
        this.prRepo = prRepo;
        this.commandHistory = commandHistory;
    }

    // POST /api/reviews/{prId}/submit
    @PostMapping("/{prId}/submit")
    public ResponseEntity<?> submit(@PathVariable Long prId) {
        try {
            PullRequest pr = prRepo.findById(prId)
                    .orElseThrow(() -> new RuntimeException("PR not found: " + prId));
            pr.getState().submit(pr);
            prRepo.save(pr);
            return ResponseEntity.ok(Map.of("status", pr.getStatus()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Reviewer name is required"));

            PullRequest pr = prRepo.findById(prId)
                    .orElseThrow(() -> new RuntimeException("PR not found: " + prId));

            ReviewCommand cmd = new ApproveCommand(pr, reviewer);
            cmd.execute();
            commandHistory.push(cmd);
            prRepo.save(pr);

            return ResponseEntity.ok(Map.of(
                    "status", pr.getStatus(),
                    "message", cmd.getDescription()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Reviewer name is required"));

            PullRequest pr = prRepo.findById(prId)
                    .orElseThrow(() -> new RuntimeException("PR not found: " + prId));

            ReviewCommand cmd = new RejectCommand(pr, reviewer);
            cmd.execute();
            commandHistory.push(cmd);
            prRepo.save(pr);

            return ResponseEntity.ok(Map.of(
                    "status", pr.getStatus(),
                    "message", cmd.getDescription()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Reviewer name is required"));

            PullRequest pr = prRepo.findById(prId)
                    .orElseThrow(() -> new RuntimeException("PR not found: " + prId));

            pr.getState().merge(pr, reviewer);
            prRepo.save(pr);

            return ResponseEntity.ok(Map.of(
                    "status", pr.getStatus(),
                    "message", reviewer + " merged PR #" + prId
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/reviews/{prId}/comment
    // Body: { "author": "keya", "lineNumber": 10, "content": "Fix this" }
    @PostMapping("/{prId}/comment")
    public ResponseEntity<?> comment(@PathVariable Long prId,
                                     @RequestBody Map<String, String> body) {
        try {
            String author = body.get("author");
            String content = body.get("content");
            String lineNumberStr = body.get("lineNumber");

            if (author == null || author.isBlank())
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Author is required"));
            if (content == null || content.isBlank())
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Comment content is required"));
            if (lineNumberStr == null)
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "lineNumber is required"));

            int lineNumber = Integer.parseInt(lineNumberStr);

            PullRequest pr = prRepo.findById(prId)
                    .orElseThrow(() -> new RuntimeException("PR not found: " + prId));

            Review review = new Review(author);
            ReviewCommand cmd = new CommentCommand(pr, review, author, lineNumber, content);
            cmd.execute();
            commandHistory.push(cmd);
            prRepo.save(pr);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", cmd.getDescription()
            ));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "lineNumber must be a number"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/reviews/undo
    @PostMapping("/undo")
    public ResponseEntity<?> undo() {
        try {
            commandHistory.undo();
            return ResponseEntity.ok(Map.of("message", "Last action undone"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/reviews/history
    @GetMapping("/history")
    public ResponseEntity<List<String>> history() {
        return ResponseEntity.ok(commandHistory.getHistory());
    }

}
