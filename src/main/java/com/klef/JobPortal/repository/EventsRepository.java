package com.klef.JobPortal.repository;

import com.klef.JobPortal.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
}
