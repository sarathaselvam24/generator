package org.nulogic.invoice.controller;

import java.math.BigDecimal;
import java.util.List;

import org.nulogic.invoice.model.Employee;
import org.nulogic.invoice.model.EmployeeAccountDetails;
import org.nulogic.invoice.model.MasterSalaryDetails;
import org.nulogic.invoice.model.Salarydetails;
import org.nulogic.invoice.repository.EmployeeRepository;
import org.nulogic.invoice.repository.MasterSalaryDetailsRepo;
import org.nulogic.invoice.repository.SalarydetailsRepository;
import org.nulogic.invoice.servic.AdminEmployeeService;
import org.nulogic.invoice.servic.EmployeeAccountDetailsService;
import org.nulogic.invoice.servic.EmployeeRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminLoginController {
	
	@Autowired
	public AdminEmployeeService adminService;
	
	@Autowired
	private EmployeeRepoService employeeRepoService;
	
	@Autowired
	public MasterSalaryDetailsRepo masterRepo;
	
	@Autowired
	public EmployeeRepository empRepo;
	
	@Autowired
	public SalarydetailsRepository salaryRepo;
	
	
	@Autowired
	EmployeeAccountDetailsService employeeAccountDetailsRepoService;
	
	@GetMapping("/admin")
	public String admin() {
		return "Admin";
	}
	
	@GetMapping("/createEmployeeForm")
	public String createEmployee() {
		return "createEmployeeForm";
	}
	
	@GetMapping("/viewEmployees")
	public String viewEmployee(Model model) {
		List<Employee> employeeDetails = adminService.getAllEmployees();
		model.addAttribute("employee", employeeDetails);
		return "viewEmpDetails";
	}
	
	@GetMapping("/editSalaryDetails")
	public String editSalary(Model model) {
		List<MasterSalaryDetails> employeeDetails = adminService.getAllEmployeeSalary();
		model.addAttribute("employeeSalary", employeeDetails);
		return "viewEmployeeSalary";
	}

	@PostMapping("/createEmployee")
	public String createEmployeeDetails(Model model, Employee employee, EmployeeAccountDetails accountdetails, MasterSalaryDetails salaryDetails)
	{
		System.out.println("EMP id " + employee.getEmpid());
		System.out.println("EMP obj"+ employee.toString());
		if (employee.getEmpid() != null) {
			Employee existingEmployee = employeeRepoService.fetchEmployee(employee.getEmpid());
			System.out.println(existingEmployee);
			existingEmployee = employeeRepoService.SaveEmployee(employee);
			accountdetails.setEmpid(employee.getEmpid());
			accountdetails.setEmpname(employee.getName());
			employeeAccountDetailsRepoService
			.SaveEmployeeAccountDetail(accountdetails);
			salaryDetails.setEmpid(employee.getEmpid());
			masterRepo.save(salaryDetails);
			model.addAttribute("msg", "Employee created successfully");
			return "hello";
			
		}
		return "hello";
		
	}
	
	@GetMapping("/updateSalary")
	public String updateSalary(@RequestParam("empid") String empid,Model model)
	{
		System.out.println("MODEL empid"+ empid);
		model.addAttribute("empSalaryData", masterRepo.findByEmpid(empid));
		return "editEmployeeSalary";
	}
	
	@PostMapping("/updateEmployeeSalary")
	public String updateSalarytoDB(Model model, MasterSalaryDetails empSalaryData)
	{
		masterRepo.save(empSalaryData);
		return "Admin";
		
	}
	
	@GetMapping("/paySlip")
	public String showPayslipForm() {
		
		return "paySlipForm";
	}

	@PostMapping("/generatePayslip")
	public String generatePaySlipinDB(@RequestParam("month") String month, @RequestParam("year") String year,@RequestParam("payabledays") BigDecimal payabledays,@RequestParam("paidmonth") BigDecimal paidmonth, Model model) {
	System.out.println("year " + month + year);
	List<Employee> emp = empRepo.findAll();
	for(Employee employee : emp)
	{
		Salarydetails details = null;
		String empId=employee.getEmpid();
		MasterSalaryDetails salaryDetails = masterRepo.findByEmpid(empId);
		if (salaryDetails.getShift().equalsIgnoreCase("Night")) {
			 details = new Salarydetails(salaryDetails.getBasicpay(),salaryDetails.getHouseallowance(),salaryDetails.getSpecialallowance(),BigDecimal.valueOf(1000),salaryDetails.getProvidentfund(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,payabledays,paidmonth,month,year);
		} else {
			 details = new Salarydetails(salaryDetails.getBasicpay(),salaryDetails.getHouseallowance(),salaryDetails.getSpecialallowance(),BigDecimal.valueOf(1000),salaryDetails.getProvidentfund(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,payabledays,paidmonth,month,year);
	}
		
//		Salarydetails details = new Salarydetails();
		
		details.setEmpid(salaryDetails.getEmpid());
//		details.setPayslipmonth(month);
//		details.setPayslipyear(year);
//		details.setBasicpay(salaryDetails.getBasicpay());
//		details.setHouseallowance(salaryDetails.getHouseallowance());
//		details.setSpecialallowance(salaryDetails.getSpecialallowance());
		
//		details.setSalaryadvance(BigDecimal.ZERO);
//		details.setProvidentfund(salaryDetails.getProvidentfund());
//		details.setProfessionaltax(BigDecimal.ZERO);
		salaryRepo.save(details);	
		model.addAttribute("msg", "Pay Slip Generated successfully");
		return "hello";
	}
	
	return "hello";
	}
}