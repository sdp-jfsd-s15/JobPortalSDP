package com.klef.JobPortal.repository;

import com.klef.JobPortal.model.SavedJobs;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJobs, Long> {
    SavedJobs findByUserName(String userName);
}
