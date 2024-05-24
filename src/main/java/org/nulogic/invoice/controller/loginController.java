package org.nulogic.invoice.controller;

import org.nulogic.invoice.model.Employee;
import org.nulogic.invoice.model.EmployeeAccountDetails;
import org.nulogic.invoice.model.Users;
import org.nulogic.invoice.servic.EmployeeAccountDetailsService;
import org.nulogic.invoice.servic.EmployeeRepoService;
import org.nulogic.invoice.servic.UsersRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class loginController {

	@Autowired
	private UsersRepoService userService;

	@Autowired
	EmployeeAccountDetailsService employeeAccountDetailsService;

	@Autowired
	private EmployeeRepoService employeeRepoService;

	@GetMapping("/")
	public String login() {
		return "redirect:/login";
	}

	@GetMapping("/logout")
	public String logout() {
		return "login";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
	 @GetMapping("/user")
	    public String getUser(HttpSession session, @AuthenticationPrincipal OAuth2User oauth2User,Model model) {
		  System.out.println("oauth2User.getAttributes() email "+oauth2User.getAttributes().get("email"));
		  
		  Users user = userService.fetchEmailUser(oauth2User.getAttributes().get("email").toString());
		
				if (user.getRole().equalsIgnoreCase("employee")) {
					Employee employee = employeeRepoService.fetchEmployee(user.getEmpid());
					EmployeeAccountDetails employeeAccountDetails = employeeAccountDetailsService
							.fetchEmployeeAccountDetail(user.getEmpid());

					if (employee == null) {

						model.addAttribute("employee", new Employee(user.getEmpid()));

					} else {
						model.addAttribute("employee", employee);
					}

					if (employeeAccountDetails == null) {
						model.addAttribute("employeeAccountDetails", new EmployeeAccountDetails(user.getEmpid()));

					} else {
						model.addAttribute("employeeAccountDetails", employeeAccountDetails);
					}
					model.addAttribute("payslipformvisibility", false);
					model.addAttribute("paysliperrormessage", false);
					session.setAttribute("employeeid", user.getEmpid());
					session.setAttribute("employee_email_id", user.getEmailid());
					return "Employee";
				}
				if (user.getRole().equalsIgnoreCase("Admin")) {
					session.setAttribute("admin_email_id", user.getEmailid());
					return "Admin";

				}

			
			return "login";
		
	    }
	

	@GetMapping("/onload")
	public String loadProfile(Model model, HttpSession session) {
		model.addAttribute("paysliperrormessage", false);
		model.addAttribute("payslipformvisibility", false);
		session.setAttribute("successmessage", false);
		Object employeeid = session.getAttribute("employeeid");
		if (employeeid != null) {
			Users user = userService.fetchUser(employeeid.toString());

			if (user.getRole().equalsIgnoreCase("employee")) {
				Employee employee = employeeRepoService.fetchEmployee(employeeid.toString());
				EmployeeAccountDetails employeeAccountDetails = employeeAccountDetailsService
						.fetchEmployeeAccountDetail(employeeid.toString());

				if (employee == null) {

					model.addAttribute("employee", new Employee(user.getEmpid()));

				} else {
					model.addAttribute("employee", employee);
				}

				if (employeeAccountDetails == null) {
					model.addAttribute("employeeAccountDetails", new EmployeeAccountDetails(user.getEmpid()));

				} else {
					model.addAttribute("employeeAccountDetails", employeeAccountDetails);
				}
				model.addAttribute("payslipformvisibility", false);
				model.addAttribute("paysliperrormessage", false);
				session.setAttribute("employeeid", employeeid);
				return "Employee";
			}
		}
		return "Employee";
	}

	@PostMapping("/Employee")
	public String verifyLogin(HttpSession session, @RequestParam("empid") String employeeid,
			@RequestParam("password") String password, Model model) {
		System.out.println("login");
		Users user = userService.fetchUser(employeeid);
		if (user == null) {
			model.addAttribute("LogInErrorMessage", "Employee Not Found! Please Try Again.");

			return "login";
		} else if (user.getPassword().equals(password)) {

			if (user.getRole().equalsIgnoreCase("employee")) {
				Employee employee = employeeRepoService.fetchEmployee(employeeid);
				EmployeeAccountDetails employeeAccountDetails = employeeAccountDetailsService
						.fetchEmployeeAccountDetail(employeeid);

				if (employee == null) {

					model.addAttribute("employee", new Employee(user.getEmpid()));

				} else {
					model.addAttribute("employee", employee);
				}

				if (employeeAccountDetails == null) {
					model.addAttribute("employeeAccountDetails", new EmployeeAccountDetails(user.getEmpid()));

				} else {
					model.addAttribute("employeeAccountDetails", employeeAccountDetails);
				}
				model.addAttribute("payslipformvisibility", false);
				model.addAttribute("paysliperrormessage", false);
				session.setAttribute("employeeid", employeeid);
				return "Employee";
			}
			if (user.getRole().equalsIgnoreCase("Admin")) {

				return "Admin";

			}

		} else {
			model.addAttribute("LogInErrorMessage", "Password does not match! Please Try Again.");
			return "login";
		}
		return "login";

	}

}
