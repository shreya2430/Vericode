package com.vericode.service;

/**
 * Singleton Pattern: single shared instance that tracks active review sessions.
 *
 * WHY THIS EXISTS:
 * Session state must be globally consistent across all requests. If multiple
 * instances existed with separate session maps, one part of the app could think
 * a PR is free while another part thinks it is being reviewed. Singleton ensures
 * there is exactly one shared map for the entire application lifetime.
 *
 * HOW IT WORKS:
 * The private constructor prevents external instantiation via new.
 * getInstance() is synchronized so only one instance is created even under
 * concurrent requests. The internal map uses ConcurrentHashMap so individual
 * reads and writes are thread safe without additional locking.
 *
 * startSession() registers a reviewer against a PR ID.
 * endSession() removes the session when the review is complete.
 * isActive() checks if a PR already has an active reviewer assigned.
 *
 * Note: sessions are in-memory only and reset on application restart.
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Singleton class to manage active review sessions for pull requests
public class ReviewSessionManager {
    private static ReviewSessionManager instance;
    private final Map<Long, String> activeSessions = new ConcurrentHashMap<>();

    private ReviewSessionManager() {}

    public static synchronized ReviewSessionManager getInstance() {
        if (instance == null) instance = new ReviewSessionManager();
        return instance;
    }
    public void startSession(Long prId, String reviewerId) {
        activeSessions.put(prId, reviewerId);
    }
    public void endSession(Long prId) {
        activeSessions.remove(prId);
    }
    public boolean isActive(Long prId) {
        return activeSessions.containsKey(prId);
    }
    public String getReviewer(Long prId) {
        return activeSessions.get(prId);
    }
}
