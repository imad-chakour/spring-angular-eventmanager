package com.myHR.api_sb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.myHR.api_sb.model.Employee;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
}
