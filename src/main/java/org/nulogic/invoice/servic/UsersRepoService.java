package org.nulogic.invoice.servic;

import org.nulogic.invoice.model.Users;
import org.nulogic.invoice.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersRepoService {

	@Autowired
	private UsersRepository usersRepo;

	public Users fetchUser(String employeeid) {
		return usersRepo.findByEmpid(employeeid);

	}

	public Users fetchEmailUser(String email) {
		// TODO Auto-generated method stub
		return usersRepo.findByEmailid(email);
	}

}
