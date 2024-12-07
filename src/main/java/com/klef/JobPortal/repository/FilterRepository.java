package com.klef.JobPortal.repository;

import com.klef.JobPortal.model.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilterRepository extends JpaRepository<Filter, Long> {
    Optional<Filter> findByCategory(String category);
}

