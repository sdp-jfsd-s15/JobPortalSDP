package com.klef.JobPortal.dtos;

import java.util.List;

public class FilterResponseDto {
    private List<String> jobs;
    private List<String> skills;
    private List<String> jobCategory;
    private List<String> jobType;

    // Getters and Setters
    public List<String> getJobs() {
        return jobs;
    }

    public void setJobs(List<String> jobs) {
        this.jobs = jobs;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(List<String> jobCategory) {
        this.jobCategory = jobCategory;
    }

    public List<String> getJobType() {
        return jobType;
    }

    public void setJobType(List<String> jobType) {
        this.jobType = jobType;
    }

    @Override
    public String toString() {
        return "FilterResponseDto{" +
                "jobs=" + jobs +
                ", skills=" + skills +
                ", jobCategory=" + jobCategory +
                ", jobType=" + jobType +
                '}';
    }
}

