package com.vericode.model;

import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ReviewTemplate implements Cloneable {

    @Setter
    private String name;
    private List<String> checklistItems;

    public ReviewTemplate(String name, List<String> checklistItems) {
        this.name = name;
        this.checklistItems = new ArrayList<>(checklistItems);
    }

    @Override
    public ReviewTemplate clone() {
        try {
            ReviewTemplate cloned = (ReviewTemplate) super.clone();
            cloned.checklistItems = new ArrayList<>(this.checklistItems);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }

    public String getName() { return name; }

    public List<String> getChecklistItems() { return checklistItems; }

    public void addChecklistItem(String item) {
        this.checklistItems.add(item);
    }

    @Override
    public String toString() {
        return "ReviewTemplate{name='" + name + "', items=" + checklistItems + "}";
    }
}