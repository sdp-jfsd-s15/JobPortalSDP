package com.klef.JobPortal.repository;

import com.klef.JobPortal.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    @Query(
            value = "SELECT * FROM Job j WHERE j.filters IS NOT NULL AND LOWER(j.filters) LIKE CONCAT('%', :filter, '%') LIMIT :pageSize OFFSET :offset",
            nativeQuery = true
    )
    List<Job> findByFilters(@Param("filter") String filter, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.filters IS NOT NULL AND LOWER(j.filters) LIKE %:filter%")
    int countByFilters(@Param("filter") String filter);

    @Query("SELECT j FROM Job j WHERE j.filters IS NOT NULL AND LOWER(j.filters) LIKE :filter")
    List<Job> findAllByFilterss(@Param("filter") String filter);


    @Query("SELECT j FROM Job j WHERE j.createdBy = :createdBy ORDER BY j.createdAt DESC")
    List<Job> findTop5ByCreatedByOrderByCreatedAtDesc(@Param("createdBy") String createdBy, Pageable pageable);

    int countByCreatedBy(@Param("createdBy") String createdBy);

    List<Job> findByCreatedBy(String createdBy);

    @Query(value = "SELECT * FROM job LIMIT :pageSize OFFSET :offset", nativeQuery = true)
    List<Job> findJobsWithPagination(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Query("SELECT j FROM Job j WHERE j.id IN :jobIds")
    List<Job> findJobsByIds(@Param("jobIds") List<Long> jobIds);

//    @Query("SELECT j FROM Job j WHERE " +
//            "(:type IS NULL OR j.type IN :type) AND " +
//            "(:category IS NULL OR j.category IN :category) AND " +
//            "(:skills IS NULL OR j.skills LIKE %:skills%) AND " +
//            "(:filter IS NULL OR j.filter LIKE %:filter%)")
//    List<Job> findFilteredJobs(
//            @Param("type") List<String> type,
//            @Param("category") List<String> category,
//            @Param("skills") String skills,
//            @Param("filter") String filter,
//            Pageable pageable
//    );
//
//    @Query("SELECT COUNT(j) FROM Job j WHERE " +
//            "(:type IS NULL OR j.type IN :type) AND " +
//            "(:category IS NULL OR j.category IN :category) AND " +
//            "(:skills IS NULL OR EXISTS (SELECT 1 FROM Job jb WHERE " +
//            "CONCAT(',', j.skills, ',') LIKE CONCAT('%,', :skills, ',%'))) AND " +
//            "(:filters IS NULL OR EXISTS (SELECT 1 FROM Job jf WHERE " +
//            "CONCAT(',', j.filters, ',') LIKE CONCAT('%,', :filters, ',%')))")
//    int countFilteredJobs(@Param("type") List<String> type,
//                          @Param("category") List<String> category,
//                          @Param("skills") String skills,
//                          @Param("filters") String filters);


}
