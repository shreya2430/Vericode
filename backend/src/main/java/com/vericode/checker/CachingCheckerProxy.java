package com.vericode.checker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy pattern: wraps a real CodeChecker and caches results by code content.
 */
public class CachingCheckerProxy implements CodeChecker {

    private final CodeChecker realChecker;
    private final Map<Integer, CheckResult> cache = new ConcurrentHashMap<>();

    public CachingCheckerProxy(CodeChecker realChecker) {
        this.realChecker = realChecker;
    }

    @Override
    public CheckResult check(String code) {
        if (code == null || code.isBlank()) {
            return realChecker.check(code);
        }

        int cacheKey = code.hashCode();

        CheckResult cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        CheckResult result = realChecker.check(code);
        cache.put(cacheKey, result);
        return result;
    }

    public int getCacheSize() {
        return cache.size();
    }

    public void clearCache() {
        cache.clear();
    }
}
