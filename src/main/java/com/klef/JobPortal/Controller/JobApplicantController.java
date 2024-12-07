package com.klef.JobPortal.Controller;

import com.klef.JobPortal.dtos.JobRequestAppliciantDto;
import com.klef.JobPortal.dtos.MessageDto;
import com.klef.JobPortal.dtos.UserJobResponseDto;
import com.klef.JobPortal.handler.JobApplicantHandler;
import com.klef.JobPortal.model.JobApplicants;
import com.klef.JobPortal.model.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("v1/api/jobApplicants")
public class JobApplicantController {
    private final JobApplicantHandler jobApplicantHandler;

    public JobApplicantController(JobApplicantHandler jobApplicantHandler){
        this.jobApplicantHandler = jobApplicantHandler;
    }

    @PostMapping(path = "/addApplicant/{jobId}/{professionalUserName}")
    public ResponseEntity<MessageDto> addJobApplicant(@PathVariable("jobId") Long jobId,
                                                      @PathVariable("professionalUserName") String profUserName,
                                                      @RequestBody Users user,
                                                      HttpServletRequest request
                                                      ) {
        return jobApplicantHandler.addApplicant(jobId, profUserName, user, request);
    }

    @GetMapping(path = "/getAppliedJobsByUserName")
    public ResponseEntity<UserJobResponseDto> getAppliedJobsByUserName(HttpServletRequest request) {
        return jobApplicantHandler.getAppliedJobsByUserName(request);
    }

    @GetMapping(path = "/getJobApplicantsList/{jobId}")
    public ResponseEntity<JobApplicants> getJobApplicantsList(@PathVariable("jobId") Long jobId, HttpServletRequest request) {
        return jobApplicantHandler.getJobApplicantsList(jobId, request);
    }

    @PutMapping(path = "/updateJobApplicant/{id}")
    public ResponseEntity<MessageDto> updateJobApplicant(@PathVariable("id") Long id, @RequestBody JobRequestAppliciantDto jobAppliciant, HttpServletRequest request) {
        return jobApplicantHandler.updateJobApplicant(id, jobAppliciant, request);
    }
}
