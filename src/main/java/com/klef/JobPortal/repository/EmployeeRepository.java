package com.klef.JobPortal.repository;


import com.klef.JobPortal.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
	// All Crud Database Methods
}
