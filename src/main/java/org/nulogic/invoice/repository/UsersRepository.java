package org.nulogic.invoice.repository;

import org.nulogic.invoice.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

	Users findByEmpid(String empid);

	Users findByEmailid(String email);

}
