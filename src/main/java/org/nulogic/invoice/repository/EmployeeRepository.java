package org.nulogic.invoice.repository;

import java.util.List;

import org.nulogic.invoice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

	Employee findByEmpid(String employeeid);
	
	List<Employee> findAll();


}
