package org.nulogic.invoice.servic;

import org.nulogic.invoice.model.Employee;
import org.nulogic.invoice.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeRepoService {

	@Autowired
	private EmployeeRepository employeeRepo;

	public Employee fetchEmployee(String employeeid) {
		return employeeRepo.findByEmpid(employeeid);

	}

	public Employee SaveEmployee(Employee employee) {
		return employeeRepo.save(employee);

	}

}
