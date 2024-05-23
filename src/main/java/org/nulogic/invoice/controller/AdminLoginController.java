package org.nulogic.invoice.controller;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.nulogic.invoice.model.Basicpay;
import org.nulogic.invoice.model.Employee;
import org.nulogic.invoice.model.EmployeeAccountDetails;
import org.nulogic.invoice.model.MasterSalaryDetails;
import org.nulogic.invoice.model.Overtime;
import org.nulogic.invoice.model.Salarydetails;
import org.nulogic.invoice.model.Users;
import org.nulogic.invoice.repository.BasicpayRepository;
import org.nulogic.invoice.repository.EmployeeRepository;
import org.nulogic.invoice.repository.MasterSalaryDetailsRepo;
import org.nulogic.invoice.repository.OvertimeRepo;
import org.nulogic.invoice.repository.SalarydetailsRepository;
import org.nulogic.invoice.repository.UsersRepository;
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
	public BasicpayRepository basicpayRepo;
	
	@Autowired
	public EmployeeRepository empRepo;
	
	@Autowired
	public SalarydetailsRepository salaryRepo;
	
	@Autowired
	public OvertimeRepo overTimeRepo;
	
	
	@Autowired
	EmployeeAccountDetailsService employeeAccountDetailsRepoService;
	
	@Autowired
	UsersRepository userRepo;
	
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
	
	
	@GetMapping("/overtime")
	public String overtime(Model model) {
		List<MasterSalaryDetails> employeeDetails = adminService.getAllEmployeeSalary();
		model.addAttribute("employeeSalary", employeeDetails);
		return "Overtime";
	}
	
	@GetMapping("/createEmployeeOvertime")
	public void createEmployeeOverTime(Overtime overtime) {
		
//		,@RequestParam("empid") String employeeid, @RequestParam("email") String email,@RequestParam("month") String month,@RequestParam("year") String year,@RequestParam("overtime") String overtime)
		System.out.println("employeeid "+overtime.getEmailid());
		System.out.println("email "+overtime.getEmpid());
		System.out.println("month "+overtime.getMonth());
		System.out.println("year "+overtime.getYear());
		System.out.println("overtime "+overtime.getOvertime());
		overTimeRepo.save(overtime);
	}
	
	
	@PostMapping("/createEmployee")
	public String createEmployeeDetails(Model model, Employee employee, EmployeeAccountDetails accountdetails, Basicpay basicpay)
	{
		System.out.println(basicpay.getCtc());
		System.out.println("EMP id " + employee.getEmpid());
		System.out.println("EMP id email " + employee.getEmail());
		System.out.println("EMP id default " + "NUIT");
		System.out.println("EMP obj"+ employee.toString());
		if (employee.getEmpid() != null) {
			Employee existingEmployee = employeeRepoService.fetchEmployee(employee.getEmpid());
			System.out.println(existingEmployee);
			existingEmployee = employeeRepoService.SaveEmployee(employee);
			accountdetails.setEmpid(employee.getEmpid());
			accountdetails.setEmpname(employee.getName());
			employeeAccountDetailsRepoService
			.SaveEmployeeAccountDetail(accountdetails);
			Basicpay basicpayobj = new Basicpay();
			basicpayobj.setEmpid(employee.getEmpid());
			basicpayobj.setEmailid(employee.getEmail());
			basicpayobj.setCtc(basicpay.getCtc());
			basicpayobj.setShift(basicpay.getShift());
			basicpayRepo.save(basicpayobj);
			model.addAttribute("msg", "Employee created successfully");
			Users userobj = new Users();
			userobj.setEmailid(employee.getEmail());
			userobj.setEmpid(employee.getEmpid());
			userobj.setRole("employee");
			userobj.setPassword("123");
			userRepo.save(userobj);
			return "hello";
			
		}
		return "hello";
		
	}

	/*
	 * @PostMapping("/createEmployee") public String createEmployeeDetails(Model
	 * model, Employee employee, EmployeeAccountDetails accountdetails,
	 * MasterSalaryDetails salaryDetails) { System.out.println("EMP id " +
	 * employee.getEmpid()); System.out.println("EMP id email " +
	 * employee.getEmail()); System.out.println("EMP id default " + "NUIT");
	 * System.out.println("EMP obj"+ employee.toString()); if (employee.getEmpid()
	 * != null) { Employee existingEmployee =
	 * employeeRepoService.fetchEmployee(employee.getEmpid());
	 * System.out.println(existingEmployee); existingEmployee =
	 * employeeRepoService.SaveEmployee(employee);
	 * accountdetails.setEmpid(employee.getEmpid());
	 * accountdetails.setEmpname(employee.getName());
	 * employeeAccountDetailsRepoService .SaveEmployeeAccountDetail(accountdetails);
	 * salaryDetails.setEmpid(employee.getEmpid()); masterRepo.save(salaryDetails);
	 * model.addAttribute("msg", "Employee created successfully"); Users userobj =
	 * new Users(); userobj.setEmailid(employee.getEmail());
	 * userobj.setEmpid(employee.getEmpid()); userobj.setRole("employee");
	 * userobj.setPassword("123"); userRepo.save(userobj); return "hello";
	 * 
	 * } return "hello";
	 * 
	 * }
	 */
	
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

	/*
	 * @PostMapping("/generatePayslip") public String
	 * generatePaySlipinDB(@RequestParam("month") String
	 * month, @RequestParam("year") String year,@RequestParam("payabledays")
	 * BigDecimal payabledays,@RequestParam("paidmonth") BigDecimal paidmonth, Model
	 * model) { System.out.println("year " + month + year); List<Employee> emp =
	 * empRepo.findAll(); for(Employee employee : emp) { Salarydetails details =
	 * null; String empId=employee.getEmpid(); MasterSalaryDetails salaryDetails =
	 * masterRepo.findByEmpid(empId); if
	 * (salaryDetails.getShift().equalsIgnoreCase("Night")) { details = new
	 * Salarydetails(salaryDetails.getBasicpay(),salaryDetails.getHouseallowance(),
	 * salaryDetails.getSpecialallowance(),BigDecimal.valueOf(1000),salaryDetails.
	 * getProvidentfund(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
	 * payabledays,paidmonth,month,year); } else { details = new
	 * Salarydetails(salaryDetails.getBasicpay(),salaryDetails.getHouseallowance(),
	 * salaryDetails.getSpecialallowance(),BigDecimal.valueOf(1000),salaryDetails.
	 * getProvidentfund(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
	 * payabledays,paidmonth,month,year); }
	 * 
	 * // Salarydetails details = new Salarydetails();
	 * 
	 * details.setEmpid(salaryDetails.getEmpid()); //
	 * details.setPayslipmonth(month); // details.setPayslipyear(year); //
	 * details.setBasicpay(salaryDetails.getBasicpay()); //
	 * details.setHouseallowance(salaryDetails.getHouseallowance()); //
	 * details.setSpecialallowance(salaryDetails.getSpecialallowance());
	 * 
	 * // details.setSalaryadvance(BigDecimal.ZERO); //
	 * details.setProvidentfund(salaryDetails.getProvidentfund()); //
	 * details.setProfessionaltax(BigDecimal.ZERO); salaryRepo.save(details);
	 * model.addAttribute("msg", "Pay Slip Generated successfully"); return "hello";
	 * }
	 * 
	 * return "hello"; }
	 */
	
	@GetMapping("/generatePayslip")
	public String generatePaySlipinDB(@RequestParam("month") String month, @RequestParam("year") String year,
			@RequestParam("payabledays") BigDecimal payabledays, @RequestParam("paidmonth") BigDecimal paidmonth,
			Model model) {
		System.out.println("year " + month + " " + year);
//	List<Employee> emp = empRepo.findAll();
		List<Basicpay> emp = basicpayRepo.findAll();
		System.out.println("emp " + emp.toString());
		for (Basicpay employee : emp) {
			System.out.println("employee size " + emp.size());
			System.out.println("employee.toString " + employee.toString());
			Salarydetails details = null;
			String empId = employee.getEmpid();
//		Basicpay salaryDetails = basicpayRepo.findByEmpid(empId);
//		System.out.println("salaryDetails.tostring "+salaryDetails.toString());
			Salarydetails sal = salaryRepo.findByPayslip(month, year, empId);
			if (sal != null) {
				BigDecimal basicpay = employee.getCtc()
						.multiply((BigDecimal.valueOf(40)).divide(BigDecimal.valueOf(100)));
				basicpay = basicpay.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128).setScale(2,
						RoundingMode.HALF_UP);
				BigDecimal nightshift = BigDecimal.valueOf(0);
				BigDecimal providentFund = BigDecimal.valueOf(0);
				BigDecimal houseAllowance = basicpay.multiply(BigDecimal.valueOf(30).divide(BigDecimal.valueOf(100)));
				BigDecimal specialAllowance = basicpay.multiply(BigDecimal.valueOf(30).divide(BigDecimal.valueOf(100)));
				if (employee.getShift().equalsIgnoreCase("Night")) {
					nightshift = paidmonth.multiply(BigDecimal.valueOf(200));
				}
				BigDecimal professionalTex = basicpay.multiply(BigDecimal.valueOf(12).divide(BigDecimal.valueOf(100)));
				if (month.equalsIgnoreCase("January")) {
					providentFund = basicpay.multiply(BigDecimal.valueOf(12).divide(BigDecimal.valueOf(100)));
				}
				if (month.equalsIgnoreCase("June")) {
					providentFund = basicpay.multiply(BigDecimal.valueOf(12).divide(BigDecimal.valueOf(100)));
				}
				details = new Salarydetails(sal.getEmpid(), sal.getBasicpay(), sal.getHouseallowance(),
						sal.getSpecialallowance(), nightshift, providentFund, professionalTex, BigDecimal.ZERO,
						BigDecimal.ZERO, payabledays, paidmonth, month, year);
				sal.setBasicpay(details.getBasicpay());
				sal.setHouseallowance(details.getHouseallowance());
				sal.setSpecialallowance(details.getSpecialallowance());
				sal.setOtallowance(details.getOtallowance());
				sal.setProvidentfund(details.getProvidentfund());
				sal.setProfessionaltax(details.getProfessionaltax());
				sal.setSalaryadvance(details.getSalaryadvance());
				sal.setPayabledays(details.getPayabledays());
				sal.setPaidmonth(details.getPaidmonth());
				sal.setNetpay(details.getNetpay());
				sal.setDeduction(details.getDeduction());
				sal.setTotal(details.getTotal());
				salaryRepo.save(sal);
				model.addAttribute("msg", "Pay Slip Generated successfully");
				return "hello";
			} else {
				System.out.println("salaryDetails empId " + empId);
				System.out.println("employee.toString " + employee.toString());
				System.out.println("employee.getCtc " + employee.getCtc());
				BigDecimal basicpay = employee.getCtc()
						.multiply((BigDecimal.valueOf(40)).divide(BigDecimal.valueOf(100)));
				System.out.println("basicpay 1 " + basicpay);
				basicpay = basicpay.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128).setScale(2,
						RoundingMode.HALF_UP);
				System.out.println("basicpay 2 " + basicpay);
				BigDecimal nightshift = BigDecimal.valueOf(0);
				BigDecimal providentFund = BigDecimal.valueOf(0);
				BigDecimal houseAllowance = basicpay.multiply(BigDecimal.valueOf(30).divide(BigDecimal.valueOf(100)));
				BigDecimal specialAllowance = basicpay.multiply(BigDecimal.valueOf(30).divide(BigDecimal.valueOf(100)));
				if (employee.getShift().equalsIgnoreCase("Night")) {
					nightshift = paidmonth.multiply(BigDecimal.valueOf(200));
				}
				BigDecimal professionalTex = basicpay.multiply(BigDecimal.valueOf(12).divide(BigDecimal.valueOf(100)));
				if (month.equalsIgnoreCase("January")) {
					providentFund = basicpay.multiply(BigDecimal.valueOf(12).divide(BigDecimal.valueOf(100)));
				}
				if (month.equalsIgnoreCase("June")) {
					providentFund = basicpay.multiply(BigDecimal.valueOf(12).divide(BigDecimal.valueOf(100)));
				}
				details = new Salarydetails(empId, basicpay, houseAllowance, specialAllowance, nightshift,
						providentFund, professionalTex, BigDecimal.ZERO, BigDecimal.ZERO, payabledays, paidmonth, month,
						year);
				salaryRepo.save(details);
				model.addAttribute("msg", "Pay Slip Generated successfully");
			}

//		Salarydetails details = new Salarydetails();

//		details.setEmpid(empId);
//		details.setPayslipmonth(month);
//		details.setPayslipyear(year);
//		details.setBasicpay(salaryDetails.getBasicpay());
//		details.setHouseallowance(salaryDetails.getHouseallowance());
//		details.setSpecialallowance(salaryDetails.getSpecialallowance());

//		details.setSalaryadvance(BigDecimal.ZERO);
//		details.setProvidentfund(salaryDetails.getProvidentfund());
//		details.setProfessionaltax(BigDecimal.ZERO);
			
		}
		return "hello";
	}
	
	@GetMapping("/test")
	public String exist()
	{
		String	year = "2020";
		String month = "January";
		String empid ="NUIT010";
		System.out.println(empid + month + year);
		Salarydetails sal = salaryRepo.findByPayslip(month, year, empid);
		return "errorPage";
		
	}
}