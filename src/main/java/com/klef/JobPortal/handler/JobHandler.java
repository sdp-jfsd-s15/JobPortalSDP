package com.klef.JobPortal.handler;

import com.klef.JobPortal.dtos.*;
import com.klef.JobPortal.model.Events;
import com.klef.JobPortal.model.Job;
import com.klef.JobPortal.repository.EventsRepository;
import com.klef.JobPortal.security.JwtVerifier;
import com.klef.JobPortal.services.JobService;
import com.klef.JobPortal.utils.DateUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobHandler {
    private static final Logger logger = LoggerFactory.getLogger(JobHandler.class);
    private final JobService jobService;
    private final EventsRepository eventRepo;
    private final Events event = new Events();

    public JobHandler(JobService jobService, EventsRepository eventRepo) {
        this.jobService = jobService;
        this.eventRepo = eventRepo;
    }

    public ResponseEntity<MessageDto> addJob(Job job, HttpServletRequest request) {
        logger.info("JobHandler.addJob START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "add job";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.addJob END");
        return jobService.addJob(job);
    }


    public ResponseEntity<UserJobResponseDto> getAllJobs(int pageSize, int pageNo, HttpServletRequest request) {
        logger.info("JobHandler.getAllJobs START");
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
        event.eventType = "get all jobs";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        try {
            UserJobResponseDto response = jobService.getAllJobs(pageSize, pageNo, userName);
            logger.info("JobHandler.getAllJobs END");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in JobHandler.getAllJobs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    public ResponseEntity<UserJobResponseDto> getJobsByFilters(int pageSize, int pageNo, List<String> filters, HttpServletRequest request) {
        logger.info("JobHandler.getJobsByFilters START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "filtered jobs";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.getJobsByFilters END");
        return jobService.filterJobsByFilters(pageSize, pageNo,filters);
    }

    public ResponseEntity<JobResponse> recentlyAddedJobsByUserName(String userName, HttpServletRequest request) {
        logger.info("JobHandler.recentlyAddedJobsByUserName START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "Recently added jobs";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.recentlyAddedJobsByUserName END");
        return jobService.recentlyAddedJobsByUserName(userName);
    }

    public ResponseEntity<PaginatedResponse<Job>> getAllProfessionalJobs(String userName, HttpServletRequest request) {
        logger.info("JobHandler.getAllProfessionalJobs START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "get all professional jobs";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.getAllProfessionalJobs END");
        return jobService.getAllProfessionalJobs(userName);
    }

    public ResponseEntity<MessageDto> updateJob(Long id, Job job, HttpServletRequest request) {
        logger.info("JobHandler.updateJob START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "update job";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.updateJob END");
        return jobService.updateJob(id, job);
    }

    public ResponseEntity<MessageDto> disableJob(Long id, String userName, HttpServletRequest request) {
        logger.info("JobHandler.disableJob START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "disable job";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.disableJob END");
        return jobService.disableJob(id, userName);
    }

    public ResponseEntity<MessageDto> enableJob(Long id, int vacancy, String userName, HttpServletRequest request) {
        logger.info("JobHandler.enableJob START");
        String token = request.getHeader("Authorization");
        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix

        Claims jwtClaims;
        try {
            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        event.userName = jwtClaims.get("cognito:username", String.class);
        event.eventType = "enable job";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.enableJob END");
        return jobService.enableJob(id, vacancy, userName);
    }

    public ResponseEntity<MessageDto> saveJob(Long id, HttpServletRequest request) {
        logger.info("JobHandler.saveJob START");
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
        event.eventType = "save job";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.saveJob END");
        return jobService.saveJob(id, userName);
    }

    public ResponseEntity<GetJobResponseDTO> getJob(Long id, HttpServletRequest request) {
        logger.info("JobHandler.getJob START");
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
        event.eventType = "save job";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.getJob END");
        return jobService.getJob(id, userName);
    }

    public ResponseEntity<MessageDto> unSaveJob(Long id, HttpServletRequest request) {
        logger.info("JobHandler.unSaveJob START");
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
        event.eventType = "unsave job";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.unSaveJob END");
        return jobService.unSaveJob(id, userName);
    }

    public ResponseEntity<UserJobResponseDto> getSavedJobs(HttpServletRequest request) {
        logger.info("JobHandler.getSavedJobs START");
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
        event.eventType = "get saved jobs";
        event.url = request.getRequestURI();
        event.ipAddress = request.getRemoteAddr();
        event.httpMethod = request.getMethod();
        event.createdAt = DateUtils.generateTimeStamp();

        eventRepo.save(event);
        logger.info("JobHandler.getSavedJobs END");
        return jobService.getSavedJobs(userName);
    }

//    public ResponseEntity<UserJobResponseDto> filteredJobs(int pageSize, int pageNo, FilterRequest filters, HttpServletRequest request) {
//        logger.info("JobHandler.filteredJobs START");
//        String token = request.getHeader("Authorization");
//        String jwtToken = token.substring(7);  // Removing 'Bearer ' prefix
//
//        Claims jwtClaims;
//        try {
//            jwtClaims = JwtVerifier.verifyJwt(jwtToken);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        event.userName = jwtClaims.get("cognito:username", String.class);
//        event.eventType = "filtered jobs";
//        event.url = request.getRequestURI();
//        event.ipAddress = request.getRemoteAddr();
//        event.httpMethod = request.getMethod();
//        event.createdAt = DateUtils.generateTimeStamp();
//
//        eventRepo.save(event);
//        logger.info("JobHandler.filteredJobs END");
//        return jobService.filteredJobs(pageSize, pageNo, filters);
//    }
}
