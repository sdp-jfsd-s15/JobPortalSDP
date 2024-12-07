package com.klef.JobPortal.dtos;

import com.klef.JobPortal.model.Job;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> data;
    private long totalJobs;

    // Constructors, getters, and setters
    public PaginatedResponse(List<T> data, long totalJobs) {
        this.data = data;
        this.totalJobs = totalJobs;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(long totalJobs) {
        this.totalJobs = totalJobs;
    }
}

