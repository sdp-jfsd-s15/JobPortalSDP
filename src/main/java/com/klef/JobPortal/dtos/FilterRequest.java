package com.klef.JobPortal.dtos;

import java.util.List;

public class FilterRequest {
    private List<String> type;
    private List<String> category;
    private List<String> skills;
    private List<String> filter;

    // Getters and Setters
    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }

    // Optional: Constructor for easier initialization
    public FilterRequest(List<String> type, List<String> category, List<String> skills, List<String> filter) {
        this.type = type;
        this.category = category;
        this.skills = skills;
        this.filter = filter;
    }

    public FilterRequest() {
        // Default constructor
    }

    @Override
    public String toString() {
        return "FilterRequest{" +
                "type=" + type +
                ", category=" + category +
                ", skills=" + skills +
                ", filter=" + filter +
                '}';
    }
}

