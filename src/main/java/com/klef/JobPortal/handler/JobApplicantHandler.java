package com.klef.JobPortal.handler;

import com.klef.JobPortal.dtos.JobRequestAppliciantDto;
import com.klef.JobPortal.dtos.MessageDto;
import com.klef.JobPortal.dtos.UserJobResponseDto;
import com.klef.JobPortal.model.Events;
import com.klef.JobPortal.model.JobApplicants;
import com.klef.JobPortal.model.Users;
import com.klef.JobPortal.repository.EventsRepository;
import com.klef.JobPortal.security.JwtVerifier;
import com.klef.JobPortal.services.JobApplicantService;
import com.klef.JobPortal.utils.DateUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class JobApplicantHandler {
    private static final Logger logger = LoggerFactory.getLogger(JobApplicantHandler.class);
    private final JobApplicantService jobApplicantService;
    private final EventsRepository eventRepo;
    private final Events event = new Events();

    public JobApplicantHandler(JobApplicantService jobApplicantService, EventsRepository eventRepo){
        this.jobApplicantService = jobApplicantService;
        this.eventRepo = eventRepo;
    }

    public ResponseEntity<MessageDto> addApplicant(Long jobId, String profUserName, Users user, HttpServletRequest request) {
        logger.info("JobApplicantHandler.addApplicant START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "add job applicant";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobApplicantHandler.addApplicant END");
        return jobApplicantService.addJobApplicant(jobId, profUserName, user);
    }

    public ResponseEntity<UserJobResponseDto> getAppliedJobsByUserName(HttpServletRequest request) {
        logger.info("JobApplicantHandler.getAppliedJobsByUserName START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String userName = jwtClaims.get("cognito:username", String.class);
        event.userName = userName;
        event.eventType = "get applied jobs";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobApplicantHandler.getAppliedJobsByUserName END");
        return jobApplicantService.getAppliedJobsByUserName(userName);
    }

    public ResponseEntity<JobApplicants> getJobApplicantsList(Long jobId, HttpServletRequest request) {
        logger.info("JobApplicantHandler.getJobApplicantsList START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String userName = jwtClaims.get("cognito:username", String.class);
        event.userName = userName;
        event.eventType = "get job applicants";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobApplicantHandler.getJobApplicantsList END");
        return jobApplicantService.getJobApplicantsList(jobId, userName);
    }

    public ResponseEntity<MessageDto> updateJobApplicant(Long id, JobRequestAppliciantDto jobAppliciant, HttpServletRequest request) {
        logger.info("JobApplicantHandler.updateJobApplicant START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String userName = jwtClaims.get("cognito:username", String.class);
        event.userName = userName;
        event.eventType = "update job applicant";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobApplicantHandler.updateJobApplicant END");
        return jobApplicantService.updateJobApplicant(id, jobAppliciant, userName);
    }
}
