package org.nulogic.invoice.repository;

import java.math.BigDecimal;
import java.util.List;

import org.nulogic.invoice.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface LoanRepository extends JpaRepository<Loan,Integer> {
	
	@Query(value = "SELECT * FROM loan WHERE empid = ?1 AND loanstatus =?2 AND loanrequeststatus =?3 AND remainingbalance >?4", nativeQuery = true)
	Loan findLoanrequest(String empid,String loanstatus,String loanrequeststatus,BigDecimal remainingbalance);
	
	@Query(value = "SELECT * FROM loan WHERE empid = ?1 AND loanstatus =?2 AND loanrequeststatus =?3", nativeQuery = true)
	Loan findLoanNotStaredrequest(String empid,String loanstatus,String loanrequeststatus);
	
	List<Loan> findByEmpidContainingOrLoanstatusContaining(String empid, String loanstatus);

}
