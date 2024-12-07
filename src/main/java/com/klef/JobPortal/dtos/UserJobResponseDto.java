package com.klef.JobPortal.dtos;

import java.util.List;

public class UserJobResponseDto {
    public int savedJobs;
    public int count;
    public List<UserJobDocuments> jobs;

    // Subclass to hold job details
    public static class UserJobDocuments {
        public long id;
        public String title;
        public String company;
        public String location;

        // Constructor for easy initialization
        public UserJobDocuments(long id, String title, String company, String location) {
            this.id = id;
            this.title = title;
            this.company = company;
            this.location = location;
        }
    }

    // Constructor for JobResponseDto
    public UserJobResponseDto(int count, List<UserJobDocuments> jobs) {
        this.count = count;
        this.jobs = jobs;
    }

    public UserJobResponseDto(int count, int savedJobs, List<UserJobDocuments> jobs) {
        this.count = count;
        this.jobs = jobs;
        this.savedJobs = savedJobs;
    }

    public UserJobResponseDto() {}
}
