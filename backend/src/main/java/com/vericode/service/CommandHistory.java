package com.vericode.service;

/**
 * Command Pattern: maintains a stack of executed commands and supports undo.
 *
 * WHY THIS EXISTS:
 * Without a central history store, there is no way to track what actions have
 * been taken or reverse the most recent one. This class is the single source
 * of truth for all executed commands in the current application session.
 *
 * HOW IT WORKS:
 * push() adds a command to the top of the stack after it is executed.
 * undo() pops the top command and calls its undo() method.
 * getHistory() returns all command descriptions for the history endpoint.
 *
 * Note: this is a Spring @Component so Spring injects one shared instance
 * into ReviewFacade automatically. The stack is in-memory and resets on
 * application restart — it is not persisted to the database.
 */

import org.springframework.stereotype.Component;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Component
public class CommandHistory {
    private final Deque<ReviewCommand> stack = new ArrayDeque<>();

    public void push(ReviewCommand cmd) {
        stack.push(cmd);
    }

    public void undo() {
        if (!stack.isEmpty()) stack.pop().undo();
        else throw new IllegalStateException("Nothing to undo.");
    }

    public List<String> getHistory() {
        return stack.stream()
                .map(ReviewCommand::getDescription)
                .toList();
    }
}
