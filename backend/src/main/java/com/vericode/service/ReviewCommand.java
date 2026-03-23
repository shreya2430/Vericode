package com.vericode.service;

public interface ReviewCommand {
    void execute();
    void undo();
    String getDescription();
}
