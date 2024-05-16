package org.nulogic.invoice.repository;

import org.nulogic.invoice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminEmployeeRepo extends JpaRepository<Employee, String>{

	
}

