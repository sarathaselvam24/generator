package org.nulogic.invoice.repository;

import org.nulogic.invoice.model.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OvertimeRepo extends JpaRepository<Overtime,String>{
	
	@Query(value = "SELECT * FROM overtime WHERE month = ?1 AND year =?2 AND empid =?3", nativeQuery = true)
	Overtime findByEmpid(String month,String year,String employeeid);

}
