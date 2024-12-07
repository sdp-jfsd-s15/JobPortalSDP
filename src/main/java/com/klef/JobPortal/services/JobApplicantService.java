package com.klef.JobPortal.services;

import com.klef.JobPortal.dtos.JobRequestAppliciantDto;
import com.klef.JobPortal.dtos.MessageDto;
import com.klef.JobPortal.dtos.UserJobResponseDto;
import com.klef.JobPortal.model.Job;
import com.klef.JobPortal.model.JobApplicants;
import com.klef.JobPortal.model.Users;
import com.klef.JobPortal.repository.JobApplicantRepository;
import com.klef.JobPortal.repository.JobRepository;
import com.klef.JobPortal.repository.UserRepository;
import com.klef.JobPortal.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobApplicantService {
    private static final Logger logger = LoggerFactory.getLogger(JobApplicantService.class);
    private final JobApplicantRepository jobApplicantRepo;
    private final JobRepository jobRepo;
    private final UserRepository userRepo;

    public JobApplicantService(JobApplicantRepository jobApplicantRepo, JobRepository jobRepo, UserRepository userRepo) {
        this.jobApplicantRepo = jobApplicantRepo;
        this.jobRepo = jobRepo;
        this.userRepo = userRepo;
    }

    public ResponseEntity<MessageDto> addJobApplicant(Long jobId, String profUserName, Users user) {
        try {
            logger.info("JobApplicantService.addJobApplicant START");
            Job job = jobRepo.findById(jobId).orElse(null);
            Optional<Users> existingUser = userRepo.findUserByUserName(user.userName);
            Users saveUser = null;
            if (existingUser.isPresent()) {
                saveUser = existingUser.get();
                List<Long> appJobs = saveUser.appliedJobs;
                if (appJobs == null) {
                    appJobs = new ArrayList<>();
                }
                appJobs.add(jobId);
                saveUser.appliedJobs = appJobs;
            }
            if (job == null) {
                logger.warn("Job ID not found: {}", jobId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageDto("Job not found"));
            }

            // Check if the vacancy is already filled
            if (job.appliedApplicantsSize >= job.vacancy) {
                logger.warn("Vacancy already filled for Job ID: {}", jobId);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new MessageDto("Job vacancy is already filled"));
            }

            // Check if JobApplicants entry exists for the given jobId
            JobApplicants existingJobApplicants = jobApplicantRepo.findByJobId(jobId).orElse(null);

            if (existingJobApplicants != null) {
                logger.info("Job ID found in the database: {}", jobId);

                // Validate professional field
                if (!existingJobApplicants.professional.equals(profUserName)) {
                    logger.warn("Professional mismatch: Expected {}, Received {}",
                            existingJobApplicants.professional, profUserName);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new MessageDto("Professional mismatch"));
                }

                // Add the user to the applicants list
                List<Users> applicants = existingJobApplicants.getApplicants();
                if (applicants.stream().anyMatch(applicant -> applicant.userName.equals(user.userName))) {
                    logger.info("User already exists in applicants list");
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new MessageDto("User already applied for this job"));
                }
                applicants.add(user);
                existingJobApplicants.setApplicants(applicants);
                existingJobApplicants.updatedAt = DateUtils.generateTimeStamp();

                job.appliedApplicantsSize += 1;
                job.updatedAt = DateUtils.generateTimeStamp();

                // Save updated JobApplicants object
                jobApplicantRepo.save(existingJobApplicants);
                jobRepo.save(job);
                assert saveUser != null;
                userRepo.save(saveUser);
                logger.info("Applicant added successfully");
                return ResponseEntity.ok(new MessageDto("Applicant added successfully"));
            } else {
                logger.info("Job ID not found. Creating new JobApplicants entry");

                // Create a new JobApplicants entry
                JobApplicants newJobApplicants = new JobApplicants();
                newJobApplicants.jobId = jobId;
                newJobApplicants.professional = profUserName;
                newJobApplicants.createdAt = DateUtils.generateTimeStamp();
                newJobApplicants.updatedAt = DateUtils.generateTimeStamp();

                // Set applicants with the new user
                List<Users> newApplicants = List.of(user);
                newJobApplicants.setApplicants(newApplicants);

                job.appliedApplicantsSize += 1;
                job.updatedAt = DateUtils.generateTimeStamp();

                // Save the new entry
                jobApplicantRepo.save(newJobApplicants);
                jobRepo.save(job);
                assert saveUser != null;
                userRepo.save(saveUser);
                logger.info("New JobApplicants entry created successfully");
                return ResponseEntity.ok(new MessageDto("JobApplicants entry created and applicant added successfully"));
            }
        } catch (Exception e) {
            logger.error("Error in JobApplicantService.addJobApplicant: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDto("Failed to add job applicant"));
        }
    }

    public ResponseEntity<UserJobResponseDto> getAppliedJobsByUserName(String userName) {
        try {
            logger.info("JobApplicantService.getAppliedJobsByUserName START");

            // Fetch user by userName
            Users user = userRepo.findUserByUserName(userName).orElse(null);
            if (user == null) {
                logger.warn("User not found: {}", userName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new UserJobResponseDto());
            }

            // Fetch job IDs from user's appliedJobs
            List<Long> jobIds = user.appliedJobs;
            if (jobIds == null || jobIds.isEmpty()) {
                logger.info("No applied jobs found for user: {}", userName);
                return ResponseEntity.ok(new UserJobResponseDto(0, List.of()));
            }

            // Retrieve jobs by IDs
            List<Job> jobs = jobRepo.findJobsByIds(jobIds);

            // Map jobs to UserJobResponseDto
            List<UserJobResponseDto.UserJobDocuments> jobDocuments = jobs.stream()
                    .map(job -> new UserJobResponseDto.UserJobDocuments(
                            job.id,
                            job.title,
                            job.company,
                            job.location
                    ))
                    .toList();

            // Create and return response
            UserJobResponseDto responseDto = new UserJobResponseDto(jobDocuments.size(), jobDocuments);
            logger.info("JobApplicantService.getAppliedJobsByUserName END");
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            logger.error("Error in JobApplicantService.getAppliedJobsByUserName: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserJobResponseDto());
        }
    }

    public ResponseEntity<JobApplicants> getJobApplicantsList(Long jobId, String userName) {
        try {
            logger.info("JobApplicantService.getJobApplicantsList START");
            Optional<JobApplicants> jobApplicants = jobApplicantRepo.findByJobId(jobId);
            if (jobApplicants.isEmpty()) {
                logger.info("JobApplicants not found for job ID: {}", jobId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            if (!jobApplicants.get().professional.equals(userName)) {
                logger.warn("User mismatch: Expected {}, Received {}", jobApplicants.get().professional, userName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
            logger.info("JobApplicantService.getJobApplicantsList END");
            JobApplicants responseJobApplicants = jobApplicants.get();
            return ResponseEntity.ok(responseJobApplicants);
        } catch (Exception e) {
            logger.error("Error in JobApplicantService.getJobApplicantsList: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    public ResponseEntity<MessageDto> updateJobApplicant(Long jobId, JobRequestAppliciantDto jobAppliciant, String userName) {
        try {
            logger.info("JobApplicantService.updateJobApplicant START");

            // Step 1: Retrieve the JobApplicants object by jobId
            Optional<JobApplicants> optionalJobApplicants = jobApplicantRepo.findByJobId(jobId);
            if (optionalJobApplicants.isEmpty()) {
                logger.info("JobApplicants not found for job ID: {}", jobId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageDto("JobApplicants not found for job ID: " + jobId));
            }

            JobApplicants jobApplicants = optionalJobApplicants.get();

            // Step 2: Verify if the `professional` matches the provided `userName`
            if (!jobApplicants.professional.equals(userName)) {
                logger.warn("User mismatch: Expected {}, Received {}", jobApplicants.professional, userName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageDto("User mismatch"));
            }

            // Step 3: Map selectedApplicants and rejectedApplicants from the request body
            jobApplicants.setSelectedApplicants(jobAppliciant.selectedApplicants);
            jobApplicants.setRejectedApplicants(jobAppliciant.rejectedApplicants);

            // Step 4: Update the updatedAt timestamp
            jobApplicants.updatedAt = DateUtils.generateTimeStamp();

            // Step 5: Save the updated object to the database
            jobApplicantRepo.save(jobApplicants);

            logger.info("JobApplicantService.updateJobApplicant END");
            return ResponseEntity.ok(new MessageDto("JobApplicants updated successfully"));
        } catch (Exception e) {
            logger.error("Error in JobApplicantService.updateJobApplicant: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDto("Failed to update job applicants"));
        }
    }

}
