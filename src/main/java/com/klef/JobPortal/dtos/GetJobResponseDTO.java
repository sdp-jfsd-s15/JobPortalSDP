package com.klef.JobPortal.dtos;

import com.klef.JobPortal.model.Job;

public class GetJobResponseDTO {
    public boolean isSaved;

    public Job jobData;

    public GetJobResponseDTO() {}

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public Job getJobData() {
        return jobData;
    }

    public void setJobData(Job jobData) {
        this.jobData = jobData;
    }
}
