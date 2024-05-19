package org.nulogic.invoice.repository;

import org.nulogic.invoice.model.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OvertimeRepo extends JpaRepository<Overtime,String>{

}
