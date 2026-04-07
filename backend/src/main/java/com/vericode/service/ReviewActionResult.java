package com.vericode.service;

/**
 * Facade Pattern: return type for ReviewFacade actions.
 *
 * Carries the two pieces of data the controller needs after any review action:
 * the new PR status (to include in the JSON response) and a human-readable
 * description of what just happened (from the Command pattern).
 *
 * Using a record keeps this immutable and avoids boilerplate getters.
 */
public record ReviewActionResult(String status, String message) {}
