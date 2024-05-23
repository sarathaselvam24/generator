package org.nulogic.invoice.repository;

import java.math.BigDecimal;

import org.nulogic.invoice.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface LoanRepository extends JpaRepository<Loan,Integer> {
	
	@Query(value = "SELECT * FROM loan WHERE empid = ?1 AND loanstatus =?2 AND loanrequeststatus =?3 AND remainingbalance >?4", nativeQuery = true)
	Loan findLoanrequest(String empid,String loanstatus,String loanrequeststatus,BigDecimal remainingbalance);
	
	@Query(value = "SELECT * FROM loan WHERE empid = ?1 AND loanstatus =?2 AND loanrequeststatus =?3", nativeQuery = true)
	Loan findLoanNotStaredrequest(String empid,String loanstatus,String loanrequeststatus);
	@Modifying
	@Query(value = "UPDATE loan SET loanrequeststatus = ?2 WHERE id = ?1);", nativeQuery = true)
	Loan updateLoanRequestStatus(int id,String loanrequeststatus);

}
