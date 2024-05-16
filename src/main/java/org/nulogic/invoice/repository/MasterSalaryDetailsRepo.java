package org.nulogic.invoice.repository;

import org.nulogic.invoice.model.MasterSalaryDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterSalaryDetailsRepo extends JpaRepository<MasterSalaryDetails, String> {
	
	MasterSalaryDetails findByEmpid(String employeeid);

}
