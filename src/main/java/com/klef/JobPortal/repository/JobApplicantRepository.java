package com.klef.JobPortal.repository;

import com.klef.JobPortal.model.JobApplicants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicantRepository extends JpaRepository<JobApplicants, Long> {
    Optional<JobApplicants> findByJobId(Long jobId);

    @Query("SELECT ja FROM JobApplicants ja WHERE ja.applicantsJson LIKE %:userName%")
    List<JobApplicants> findByUserNameInApplicants(@Param("userName") String userName);

    // JobApplicants findByJobIdAndProfessional(Long jobId, String userName);
}
