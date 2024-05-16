package org.nulogic.invoice.servic;

import java.util.List;

import org.nulogic.invoice.model.Employee;
import org.nulogic.invoice.model.MasterSalaryDetails;
import org.nulogic.invoice.repository.AdminEmployeeRepo;
import org.nulogic.invoice.repository.MasterSalaryDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminEmployeeService {
	
	@Autowired
    private AdminEmployeeRepo adminRepo;
	
	@Autowired
	private MasterSalaryDetailsRepo repo;

    public List<Employee> getAllEmployees() {
        return adminRepo.findAll();
    }

    public List<MasterSalaryDetails> getAllEmployeeSalary()
    {
    	return repo.findAll();
    }
}
