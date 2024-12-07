package com.klef.JobPortal.services;

import com.klef.JobPortal.dtos.*;
import com.klef.JobPortal.exception.ResourceNotFoundException;
import com.klef.JobPortal.model.Filter;
import com.klef.JobPortal.model.FilterItem;
import com.klef.JobPortal.model.Job;
import com.klef.JobPortal.model.SavedJobs;
import com.klef.JobPortal.repository.FilterRepository;
import com.klef.JobPortal.repository.JobRepository;
import com.klef.JobPortal.repository.SavedJobRepository;
import com.klef.JobPortal.utils.DateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);
    private final JobRepository jobRepo;
    private final FilterRepository filterRepo;
    private final SavedJobRepository savedJobRepo;

    public JobService(JobRepository jobRepo, FilterRepository filterRepo, SavedJobRepository savedJobRepo) {
        this.filterRepo = filterRepo;
        this.jobRepo = jobRepo;
        this.savedJobRepo = savedJobRepo;
    }

    public ResponseEntity<MessageDto> addJob(Job job) {
        try {
            logger.info("JobService.addJob START");

            // Set timestamps and default values for the job
            job.createdAt = DateUtils.generateTimeStamp();
            job.updatedAt = DateUtils.generateTimeStamp();
            job.isPublish = true;

            logger.info("Received job with skills: {}", job.skills);
            logger.info("Received job with filters: {}", job.filters);

            // Process filters for different categories
            saveOrValidateFilters("jobs", List.of(job.experience));
            saveOrValidateFilters("skills", job.skills);
            saveOrValidateFilters("jobCategory", List.of(job.category));
            saveOrValidateFilters("jobType", List.of(job.type));

            List<String> combinedFilters = new ArrayList<>();
            if (job.type != null) combinedFilters.add(job.type);
            if (job.category != null) combinedFilters.add(job.category);
            if (job.experience != null) combinedFilters.add(job.experience);
            if (job.skills != null) combinedFilters.addAll(job.skills);

            job.filters = combinedFilters;

            // Save the job entity
            jobRepo.save(job);

            MessageDto responseMessage = new MessageDto("Job added successfully");
            logger.info("JobService.addJob END");
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            logger.error("Error in JobService.addJob: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDto("Failed to add job"));
        }
    }

    /**
     * Helper method to save filters or validate their existence in the database.
     */
    private void saveOrValidateFilters(String category, List<String> filterNames) {
        // Fetch the existing filter for the given category
        Filter filter = filterRepo.findByCategory(category).orElseGet(() -> {
            // Create a new Filter for the category if it doesn't exist
            Filter newFilter = new Filter();
            newFilter.category = category;
            newFilter.filterItems = new ArrayList<>();
            filterRepo.save(newFilter);
            return newFilter;
        });

        // Process each filter name
        for (String filterName : filterNames) {
            // Check if the filter item already exists
            boolean filterItemExists = filter.filterItems.stream()
                    .anyMatch(item -> item.filterName.equals(filterName));

            if (!filterItemExists) {
                // Add the new filter item if it doesn't exist
                FilterItem newFilterItem = new FilterItem();
                newFilterItem.filterName = filterName;
                newFilterItem.filter = filter;
                filter.filterItems.add(newFilterItem);
            }
        }

        // Save the updated filter (if new items were added)
        filterRepo.save(filter);
    }


    public UserJobResponseDto getAllJobs(int pageSize, int pageNo, String userName) {
        logger.info("JobService.getAllJobs START");

        // Calculate offset for pagination
        int offset = (pageNo - 1) * pageSize;
        List<Job> jobs = jobRepo.findJobsWithPagination(offset, pageSize);
        long totalCount = jobRepo.count(); // Total number of rows in the database

        // Fetch the saved jobs for the user
        SavedJobs savedJobByUser = savedJobRepo.findByUserName(userName);

        // Handle the case where no saved jobs are found for the user
        int savedSize = (savedJobByUser != null && savedJobByUser.savedJobs != null)
                ? savedJobByUser.savedJobs.size()
                : 0;

        // Map jobs to the UserJobDocuments format
        List<UserJobResponseDto.UserJobDocuments> jobDetails = jobs.stream()
                .map(job -> new UserJobResponseDto.UserJobDocuments(
                        job.id,
                        job.title,
                        job.company,
                        job.location
                ))
                .toList();

        logger.info("JobService.getAllJobs END");
        return new UserJobResponseDto((int) totalCount, savedSize, jobDetails);
    }


    public ResponseEntity<UserJobResponseDto> filterJobsByFilters(
            int pageSize, int pageNo, List<String> filters) {
        try {
            // Pagination setup
            int offset = (pageNo - 1) * pageSize;

            // If no filters are provided, return all jobs with pagination
            if (filters == null || filters.isEmpty()) {
                List<Job> allJobs = jobRepo.findJobsWithPagination(offset, pageSize);
                List<UserJobResponseDto.UserJobDocuments> jobDocuments = allJobs.stream()
                        .map(job -> new UserJobResponseDto.UserJobDocuments(
                                job.id,
                                job.title,
                                job.company,
                                job.location
                        ))
                        .collect(Collectors.toList());

                return ResponseEntity.ok(new UserJobResponseDto(
                        (int) jobRepo.count(),
                        jobDocuments
                ));
            }

            // Use a Set to collect unique jobs
            Set<Job> filteredJobs = new HashSet<>();

            for (String filter : filters) {
                String formattedFilter = "%" + filter.toLowerCase() + "%";
                List<Job> paginatedJobs = jobRepo.findByFilters(formattedFilter, offset, pageSize);
                filteredJobs.addAll(paginatedJobs);
            }

            // Map to DTO format
            List<UserJobResponseDto.UserJobDocuments> jobDocuments = filteredJobs.stream()
                    .map(job -> new UserJobResponseDto.UserJobDocuments(
                            job.id,
                            job.title,
                            job.company,
                            job.location
                    ))
                    .toList();

            return ResponseEntity.ok(new UserJobResponseDto(
                    (int) jobRepo.count(),
                    jobDocuments
            ));

        } catch (Exception e) {
            logger.error("Error filtering jobs by filters", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserJobResponseDto(0, List.of()));
        }
    }

    public ResponseEntity<JobResponse> recentlyAddedJobsByUserName(String userName) {
        try {
            logger.info("JobService.recentlyAddedJobsByUserName START");
            int totalDocuments = jobRepo.countByCreatedBy(userName);
            Pageable pageable = PageRequest.of(0, 5);
            List<Job> jobs = jobRepo.findTop5ByCreatedByOrderByCreatedAtDesc(userName,pageable);
            JobResponse response = new JobResponse(totalDocuments, jobs);
            logger.info("JobService.recentlyAddedJobsByUserName END");
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<PaginatedResponse<Job>> getAllProfessionalJobs(String userName) {
        try {
            logger.info("JobService.getAllProfessionalJobs START");
            int totalDocuments = jobRepo.countByCreatedBy(userName);
            List<Job> jobs = jobRepo.findByCreatedBy(userName);
            PaginatedResponse<Job> response = new PaginatedResponse<>(jobs, totalDocuments);
            logger.info("JobService.getAllProfessionalJobs END");
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<MessageDto> updateJob(Long id, Job job) {
        try {
            logger.info("JobService.updateJob START");
            Optional<Job> existingJob = jobRepo.findById(id);
            if (existingJob.isPresent()) {
                Job updatedJob = existingJob.get();
                updatedJob.company = job.company;
                updatedJob.title = job.title;
                updatedJob.description = job.description;
                updatedJob.qualifications = job.qualifications;
                updatedJob.vacancy = job.vacancy;
                updatedJob.location = job.location;
                updatedJob.type = job.type;
                updatedJob.category = job.category;
                updatedJob.salary = job.salary;
                updatedJob.experience = job.experience;
                updatedJob.skills = job.skills;
                updatedJob.filters = job.filters;
                updatedJob.updatedAt = DateUtils.generateTimeStamp();
                jobRepo.save(updatedJob);
                MessageDto responseMessage = new MessageDto("Job updated successfully");
                logger.info("JobService.updateJob END");
                return ResponseEntity.ok(responseMessage);
            } else {
                MessageDto errorMessage = new MessageDto("Job not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            }
        }
        catch (Exception e){
            MessageDto errorMessage = new MessageDto("Failed to update Job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    public ResponseEntity<MessageDto> disableJob(Long id, String userName) {
        try {
            logger.info("JobService.disableJob START");

            // Find job by ID
            Optional<Job> existingJob = jobRepo.findById(id);
            if (existingJob.isPresent()) {
                Job updatedJob = existingJob.get();

                // Set isPublish based on status
                if(Objects.equals(updatedJob.createdBy, userName)) {
                    updatedJob.isPublish = false;
                    updatedJob.vacancy = 0;
                    updatedJob.updatedAt = DateUtils.generateTimeStamp();

                    jobRepo.save(updatedJob);

                    MessageDto responseMessage = new MessageDto("Job updated successfully");
                    logger.info("JobService.disableJob END");
                    return ResponseEntity.ok(responseMessage);
                }
                else{
                    MessageDto errorMessage = new MessageDto("You are not authorized to update this job");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
                }
            } else {
                MessageDto errorMessage = new MessageDto("Job not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            }
        } catch (Exception e) {
            logger.error("JobService.disableJob ERROR: ", e);
            MessageDto errorMessage = new MessageDto("Failed to update Job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    public ResponseEntity<MessageDto> enableJob(Long id, int vacancy, String userName) {
        try {
            logger.info("JobService.enableJob START");

            // Find job by ID
            Optional<Job> existingJob = jobRepo.findById(id);
            if (existingJob.isPresent()) {
                Job updatedJob = existingJob.get();

                // Set isPublish based on status
                if(Objects.equals(updatedJob.createdBy, userName)) {
                    updatedJob.isPublish = true;
                    updatedJob.vacancy = vacancy;
                    updatedJob.updatedAt = DateUtils.generateTimeStamp();

                    jobRepo.save(updatedJob);

                    MessageDto responseMessage = new MessageDto("Job updated successfully");
                    logger.info("JobService.enableJob END");
                    return ResponseEntity.ok(responseMessage);
                }
                else{
                    MessageDto errorMessage = new MessageDto("You are not authorized to update this job");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
                }
            } else {
                MessageDto errorMessage = new MessageDto("Job not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            }
        } catch (Exception e) {
            logger.error("JobService.enableJob ERROR: ", e);
            MessageDto errorMessage = new MessageDto("Failed to update Job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    public ResponseEntity<MessageDto> saveJob(Long id, String userName) {
        try {
            logger.info("JobService.saveJob START");
            Optional<Job> existingJob = jobRepo.findById(id);
            if (existingJob.isEmpty()) {
                MessageDto errorMessage = new MessageDto("Job not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            }
            SavedJobs savedJobByUser = savedJobRepo.findByUserName(userName);
            if(savedJobByUser == null) {
                SavedJobs saveNewJob = new SavedJobs();
                saveNewJob.userName = userName;
                List<Long> jobIds = new ArrayList<>();
                jobIds.add(id);
                saveNewJob.savedJobs = jobIds;
                saveNewJob.createdAt = DateUtils.generateTimeStamp();
                saveNewJob.updatedAt = DateUtils.generateTimeStamp();
                saveNewJob.isActive = true;
                savedJobRepo.save(saveNewJob);
                logger.info("JobService.saveJob END");
                return ResponseEntity.ok(new MessageDto("Job Saved Successfully"));
            }

            if (savedJobByUser.savedJobs.contains(id)) {
                MessageDto errorMessage = new MessageDto("Job is already saved");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
            else {
                savedJobByUser.savedJobs.add(id);
                savedJobRepo.save(savedJobByUser);
                MessageDto responseMessage = new MessageDto("Job saved successfully");
                logger.info("JobService.saveJob END");
                return ResponseEntity.ok(responseMessage);
            }
        } catch (Exception e) {
            logger.error("JobService.saveJob ERROR: ", e);
            MessageDto errorMessage = new MessageDto("Failed to save Job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    public ResponseEntity<GetJobResponseDTO> getJob(Long id, String userName) {
        try {
            logger.info("JobService.getJob START");

            // Retrieve the Job by ID
            Job job = jobRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

            // Initialize response DTO
            GetJobResponseDTO jobResponse = new GetJobResponseDTO();
            jobResponse.jobData = job;

            // Retrieve SavedJobs by username
            SavedJobs savedJob = savedJobRepo.findByUserName(userName);

            // Check if the job ID is saved
            if (savedJob == null || savedJob.savedJobs == null) {
                jobResponse.isSaved = false;
            } else {
                jobResponse.isSaved = savedJob.savedJobs.contains(id);
            }

            logger.info("JobService.getJob END");
            return ResponseEntity.ok(jobResponse);
        } catch (Exception e) {
            logger.error("JobService.getJob ERROR: ", e);
            MessageDto errorMessage = new MessageDto("Failed to get job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    public ResponseEntity<MessageDto> unSaveJob(Long id, String userName) {
        try {
            logger.info("JobService.unSaveJob START");
            SavedJobs savedJobByUser = savedJobRepo.findByUserName(userName);
            if(savedJobByUser == null) {
                MessageDto errorMessage = new MessageDto("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            }
            if (savedJobByUser.savedJobs.contains(id)) {
                savedJobByUser.savedJobs.remove(id);
                savedJobRepo.save(savedJobByUser);
                MessageDto responseMessage = new MessageDto("Job unsaved successfully");
                logger.info("JobService.unSaveJob END");
                return ResponseEntity.ok(responseMessage);
            }
            else {
                MessageDto errorMessage = new MessageDto("Job is not saved");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
        } catch (Exception e) {
            logger.error("JobService.unSaveJob ERROR: ", e);
            MessageDto errorMessage = new MessageDto("Failed to unsave Job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    public ResponseEntity<UserJobResponseDto> getSavedJobs(String userName) {
        try {
            logger.info("JobService.getSavedJobs START");

            // Find saved jobs by username
            SavedJobs savedJobByUser = savedJobRepo.findByUserName(userName);
            if (savedJobByUser == null) {
                MessageDto errorMessage = new MessageDto("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Retrieve saved job IDs
            List<Long> savedJobIds = savedJobByUser.savedJobs;
            List<Job> savedJobs = jobRepo.findAllById(savedJobIds);

            // Map savedJobs to UserJobDocuments
            List<UserJobResponseDto.UserJobDocuments> jobDocuments = savedJobs.stream()
                    .map(job -> new UserJobResponseDto.UserJobDocuments(
                            job.id,
                            job.title,
                            job.company,
                            job.location
                    ))
                    .toList(); // Use toList() for modern Java (or collect(Collectors.toList()) for earlier versions)

            // Construct response DTO
            UserJobResponseDto userJobResponseDto = new UserJobResponseDto(savedJobIds.size(), jobDocuments);

            logger.info("JobService.getSavedJobs END");
            return ResponseEntity.ok(userJobResponseDto);
        } catch (Exception e) {
            logger.error("JobService.getSavedJobs ERROR: ", e);
            MessageDto errorMessage = new MessageDto("Failed to get saved jobs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

//    public ResponseEntity<UserJobResponseDto> filteredJobs(int pageSize, int pageNo, FilterRequest filters) {
//        logger.info("JobService.filteredJobs START");
//        Pageable pageable = PageRequest.of(pageNo - 1, pageSize); // Page number starts from 0 in Spring Data
//
//        // Dynamically build the filter conditions
//        Specification<Job> specification = (root, query, criteriaBuilder) -> {
//            Predicate predicate = criteriaBuilder.conjunction();
//
//            // Apply filters based on provided criteria, ensuring null or empty lists are handled
//            if (filters.getType() != null && !filters.getType().isEmpty()) {
//                predicate = criteriaBuilder.and(predicate, root.get("type").in(filters.getType()));
//            }
//            if (filters.getCategory() != null && !filters.getCategory().isEmpty()) {
//                predicate = criteriaBuilder.and(predicate, root.get("category").in(filters.getCategory()));
//            }
//            if (filters.getSkills() != null && !filters.getSkills().isEmpty()) {
//                predicate = criteriaBuilder.and(predicate, root.get("skills").in(filters.getSkills()));
//            }
//            if (filters.getFilter() != null && !filters.getFilter().isEmpty()) {
//                predicate = criteriaBuilder.and(predicate, root.get("filters").in(filters.getFilter()));
//            }
//
//            return predicate;
//        };
//
//        // Fetch filtered jobs
//        Page<Job> jobPage = jobRepo.findAll(specification, pageable);
//
//        // Map the result to DTO
//        List<UserJobResponseDto.UserJobDocuments> jobDocuments = jobPage.getContent().stream()
//                .map(job -> new UserJobResponseDto.UserJobDocuments(job.id, job.title, job.company, job.location))
//                .collect(Collectors.toList());
//
//        // Prepare the response DTO
//        UserJobResponseDto response = new UserJobResponseDto((int) jobPage.getTotalElements(), jobDocuments);
//        logger.info("JobService.filteredJobs END");
//
//        return ResponseEntity.ok(response);
//    }
