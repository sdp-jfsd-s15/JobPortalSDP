package com.klef.JobPortal.repository;

import com.klef.JobPortal.model.Connections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connections, Long> {
    @Query("SELECT c FROM Connections c JOIN c.connections ci WHERE c.userName = :userName AND ci.status = 'ACCEPTED'")
    List<Connections> findAcceptedConnectionsByUserName(@Param("userName") String userName);
    Connections findByUserName(String userName);
}
