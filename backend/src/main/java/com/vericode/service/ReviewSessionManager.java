package com.vericode.service;

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
