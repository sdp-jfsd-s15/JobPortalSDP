package com.klef.JobPortal.Controller;

import java.util.List;

import com.klef.JobPortal.exception.ResourceNotFoundException;
import com.klef.JobPortal.model.Employee;
import com.klef.JobPortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("v1/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping(path = "/all-employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @PostMapping(path = "/add")
    public Employee createEmployee(@RequestBody Employee e) {
        return employeeRepository.save(e);
    }

    @GetMapping(path = "/employee-by-id/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee ID not Found"));
        return ResponseEntity.ok(employee);
    }

    @PutMapping(path = "/update-employee/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable long id, @RequestBody Employee employeeDetails) {
        Employee updateEmployeeDetails = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee ID not Found: " + id));
        updateEmployeeDetails.setFirstName(employeeDetails.getFirstName());
        updateEmployeeDetails.setLastName(employeeDetails.getLastName());
        updateEmployeeDetails.setEmail(employeeDetails.getEmail());

        employeeRepository.save(updateEmployeeDetails);

        return ResponseEntity.ok(updateEmployeeDetails);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee Id not found to delete"));
        employeeRepository.delete(emp);

        return new ResponseEntity<>("Successfully Deleted", HttpStatus.OK);
    }
}
