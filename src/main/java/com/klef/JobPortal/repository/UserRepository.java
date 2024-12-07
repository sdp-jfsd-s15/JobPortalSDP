package com.klef.JobPortal.repository;

import com.klef.JobPortal.dtos.UserDto;
import com.klef.JobPortal.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findUserByEmail(@Param("email") String email);
    Optional<Users> findUserByUserName(@Param("userName") String userName);

    @Query("SELECT new com.klef.JobPortal.dtos.UserDto(u.id, u.userName, u.firstName, u.lastName) " +
            "FROM Users u WHERE " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchText, '%'))) AND " +
            "u.role IN ('PROFESSIONAL', 'USER')")
    List<UserDto> searchUsers(@Param("searchText") String searchText);

}
