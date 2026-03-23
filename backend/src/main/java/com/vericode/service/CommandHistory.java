package com.vericode.service;

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
