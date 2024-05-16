package org.nulogic.invoice.servic;

import org.nulogic.invoice.model.EmployeeAccountDetails;
import org.nulogic.invoice.repository.EmployeeAccountDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeAccountDetailsService {

	@Autowired
	private EmployeeAccountDetailsRepository employeeAccountRepo;

	public EmployeeAccountDetails fetchEmployeeAccountDetail(String employeeid) {
		return employeeAccountRepo.findByEmpid(employeeid);

	}

	public EmployeeAccountDetails SaveEmployeeAccountDetail(EmployeeAccountDetails existingEmployee) {
		System.out.println("save employee account detail " + existingEmployee.getAccountnumber());
		return employeeAccountRepo.save(existingEmployee);
	}

}
