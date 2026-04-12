package com.vericode.service;

/**
 * Command Pattern: defines the contract for all reversible reviewer actions.
 *
 * WHY THIS EXISTS:
 * Without the Command pattern, reviewer actions like approve and reject would
 * be executed inline and lost forever. There would be no way to undo them or
 * log them. By wrapping each action as an object with execute() and undo(),
 * every action becomes reversible and trackable.
 *
 * HOW IT WORKS:
 * Each concrete command snapshots the state it needs before executing so that
 * undo() can restore it exactly. CommandHistory maintains a stack of executed
 * commands. Calling undo() pops the top command and calls its undo() method.
 *
 * getDescription() returns a human-readable log entry used by the history
 * endpoint to show what actions have been taken on a PR.
 */


public interface ReviewCommand {
    void execute();
    void undo();
    String getDescription();
}
