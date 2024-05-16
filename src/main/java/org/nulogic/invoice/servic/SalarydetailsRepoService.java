package org.nulogic.invoice.servic;

import org.nulogic.invoice.model.Salarydetails;
import org.nulogic.invoice.repository.SalarydetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class SalarydetailsRepoService {
	
	@Autowired
	private SalarydetailsRepository salarydetailsRepoService;
	
	public Salarydetails fetchSalaryDetail(String month, String year,String employeeid) {
		return salarydetailsRepoService.findByPayslip(month,year,employeeid);
	}

}
