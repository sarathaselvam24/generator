package org.nulogic.invoice.repository;

import org.nulogic.invoice.model.EmployeeAccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeAccountDetailsRepository extends JpaRepository<EmployeeAccountDetails, String> {

	EmployeeAccountDetails findByEmpid(String empid);

}
