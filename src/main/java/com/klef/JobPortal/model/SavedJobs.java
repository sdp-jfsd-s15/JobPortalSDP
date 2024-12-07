package com.klef.JobPortal.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "saved_job")
public class SavedJobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long savedId;
    public String userName;
    public List<Long> savedJobs;
    public String createdAt;
    public String updatedAt;
    public boolean isActive;

    public SavedJobs() {}
}
