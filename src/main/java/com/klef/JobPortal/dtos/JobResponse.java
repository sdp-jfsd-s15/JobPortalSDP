package com.klef.JobPortal.dtos;

import com.klef.JobPortal.model.Job;

import java.util.List;

// Create a custom response DTO class
public class JobResponse {
    private int totalDocuments;
    private List<Job> recentRecords;

    // Constructor
    public JobResponse(int totalDocuments, List<Job> recentRecords) {
        this.totalDocuments = totalDocuments;
        this.recentRecords = recentRecords;
    }

    // Getters and Setters
    public int getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(int totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public List<Job> getRecentRecords() {
        return recentRecords;
    }

    public void setRecentRecords(List<Job> recentRecords) {
        this.recentRecords = recentRecords;
    }
}

