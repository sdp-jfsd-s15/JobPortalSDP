package com.klef.JobPortal.Controller;

import com.klef.JobPortal.dtos.*;
import com.klef.JobPortal.handler.JobHandler;
import com.klef.JobPortal.model.Job;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("v1/api/job")
public class JobController {
    private final JobHandler jobHandler;

    public JobController(JobHandler jobHandler) {
        this.jobHandler = jobHandler;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<MessageDto> addJob(@RequestBody Job job, HttpServletRequest request) {
        return jobHandler.addJob(job, request);
    }

    @PostMapping(path = "/saveJob/{id}")
    public ResponseEntity<MessageDto> saveJob(@PathVariable("id") Long id, HttpServletRequest request) {
        return jobHandler.saveJob(id, request);
    }

    @PutMapping(path = "/unSaveJob/{id}")
    public ResponseEntity<MessageDto> unSaveJob(@PathVariable("id") Long id, HttpServletRequest request) {
        return jobHandler.unSaveJob(id, request);
    }


    @GetMapping(path = "/getJob/{id}")
    public ResponseEntity<GetJobResponseDTO> getJob(@PathVariable("id") Long id, HttpServletRequest request) {
        return jobHandler.getJob(id, request);
    }

    @GetMapping(path = "/recentlyAddedJobsByUserName/{userName}")
    public ResponseEntity<JobResponse> recentlyAddedJobsByUserName(@PathVariable("userName") String userName, HttpServletRequest request) {
        return jobHandler.recentlyAddedJobsByUserName(userName, request);
    }

    @GetMapping(path = "/getAllJobs")
    public ResponseEntity<UserJobResponseDto> getAllJobs(
            @RequestParam(name = "page_size", required = false, defaultValue = "2") int pageSize,
            @RequestParam(name = "page_no", required = false, defaultValue = "1") int pageNo,
            HttpServletRequest request) {
        return jobHandler.getAllJobs(pageSize, pageNo, request);
    }

    @GetMapping(path = "/getSavedJobs")
    public ResponseEntity<UserJobResponseDto> getSavedJobs(
            HttpServletRequest request) {
        return jobHandler.getSavedJobs(request);
    }

    @PostMapping("/filter")
    public ResponseEntity<UserJobResponseDto> getJobsByFilters(
            @RequestParam(name = "page_size", required = false, defaultValue = "2") int pageSize,
            @RequestParam(name = "page_no", required = false, defaultValue = "1") int pageNo,
            @RequestBody List<String> filters, HttpServletRequest request) {
        return jobHandler.getJobsByFilters(pageSize, pageNo, filters, request);
    }

    @GetMapping(path = "/getAllProfessionalJobs/{userName}")
    public ResponseEntity<PaginatedResponse<Job>> getAllProfessionalJobs(@PathVariable("userName") String userName, HttpServletRequest request) {
        return jobHandler.getAllProfessionalJobs(userName, request);
    }

    @PutMapping(path = "/updateJob/{id}")
    public ResponseEntity<MessageDto> updateUser(@PathVariable("id") Long id, @RequestBody Job job, HttpServletRequest request) {
        return jobHandler.updateJob(id, job, request);
    }

    @PutMapping(path = "/disableJob")
    public ResponseEntity<MessageDto> disableJob(@RequestParam("id") Long id, @RequestParam("userName") String userName, HttpServletRequest request) {
        return jobHandler.disableJob(id, userName, request);
    }

    @PutMapping(path = "/enableJob")
    public ResponseEntity<MessageDto> enableJob(@RequestParam("id") Long id, @RequestParam("vacancy") int vacancy, @RequestParam("userName") String userName, HttpServletRequest request) {
        return jobHandler.enableJob(id, vacancy, userName, request);
    }
}
