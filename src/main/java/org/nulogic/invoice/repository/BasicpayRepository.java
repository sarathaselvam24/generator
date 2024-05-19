package org.nulogic.invoice.repository;

import java.util.List;

import org.nulogic.invoice.model.Basicpay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface BasicpayRepository extends JpaRepository<Basicpay,String>{
	
	Basicpay findByEmpid(String employeeid);
	
	List<Basicpay> findAll();

}
