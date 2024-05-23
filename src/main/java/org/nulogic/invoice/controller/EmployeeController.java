package org.nulogic.invoice.controller;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.nulogic.invoice.model.Basicpay;
import org.nulogic.invoice.model.Employee;
import org.nulogic.invoice.model.EmployeeAccountDetails;
import org.nulogic.invoice.model.Loan;
import org.nulogic.invoice.model.Salarydetails;
import org.nulogic.invoice.repository.BasicpayRepository;
import org.nulogic.invoice.repository.LoanRepository;
import org.nulogic.invoice.servic.EmployeeAccountDetailsService;
import org.nulogic.invoice.servic.EmployeeRepoService;
import org.nulogic.invoice.servic.SalarydetailsRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class EmployeeController {

	@Autowired
	private EmployeeRepoService employeeRepoService;

	@Autowired
	EmployeeAccountDetailsService employeeAccountDetailsRepoService;

	@Autowired
	SalarydetailsRepoService salarydetailsRepoService;

	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
    private JavaMailSender emailSender;
	
	@Autowired
	private BasicpayRepository basicRepo;

	@Autowired
	private LoanRepository loanRepo;

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	@PostMapping("/employee")
	public String saveEmployee(HttpSession session, Employee employee, Model model) {
		if (employee.getEmpid() != null) {
			EmployeeAccountDetails existingEmployeeAccount = employeeAccountDetailsRepoService
					.fetchEmployeeAccountDetail(employee.getEmpid());
			Employee existingEmployee = employeeRepoService.SaveEmployee(employee);
			model.addAttribute("employee", existingEmployee);
			if (existingEmployeeAccount == null) {
				model.addAttribute("employeeAccountDetails", new EmployeeAccountDetails(employee.getEmpid()));

			} else {
				model.addAttribute("employeeAccountDetails", existingEmployeeAccount);
			}
		}
		model.addAttribute("paysliperrormessage", false);
		model.addAttribute("payslipformvisibility", false);
		session.setAttribute("successmessage", false);
		return "EmployeeSuccess";
	}

	@Transactional
	@PostMapping("/employeeAccountDetails")
	public String saveEmployeeAccountDetails(HttpSession session, EmployeeAccountDetails employeeAccountDetail,
			Model model) {
		if (employeeAccountDetail.getEmpid() != null) {
			Employee employee = employeeRepoService.fetchEmployee(employeeAccountDetail.getEmpid());

			if (employee != null) {
				model.addAttribute("employee", employee);
			} else {
				model.addAttribute("employee", new Employee(employeeAccountDetail.getEmpid()));
			}

			EmployeeAccountDetails existingEmployeeAccount = employeeAccountDetailsRepoService
					.SaveEmployeeAccountDetail(employeeAccountDetail);

			model.addAttribute("employeeAccountDetails", existingEmployeeAccount);
		}
		model.addAttribute("payslipformvisibility", false);
		model.addAttribute("paysliperrormessage", false);
		session.setAttribute("successmessage", false);
		return "EmployeeSuccess";
	}

	@PostMapping("/paysliprequest")
	public String payslipRequest(HttpSession session, @RequestParam("payslip") String payslipdate,
			@RequestParam("empid") String employeeid, Model model) {
		String[] parts = payslipdate.split(" ");
		String month = parts[0];
		String year = parts[1];
		System.out.println("month " + month + " year " + year);
		model.addAttribute("payslipformvisibility", true);
		model.addAttribute("paysliperrormessage", false);
		model.addAttribute("successmessage", false);
		Salarydetails salarydetails = salarydetailsRepoService.fetchSalaryDetail(month, year, employeeid);
		if (salarydetails == null) {
			model.addAttribute("paysliperrormessage", true);
			EmployeeAccountDetails employeeAccountDetail = employeeAccountDetailsRepoService
					.fetchEmployeeAccountDetail(employeeid);

			Employee employee = employeeRepoService.fetchEmployee(employeeid);

			if (salarydetails == null && employee == null) {
				model.addAttribute("employee", new Employee(employeeid));
			}

			if (salarydetails == null && employee != null) {
				model.addAttribute("employee", employee);
			}

			if (salarydetails == null && employeeAccountDetail == null) {
				model.addAttribute("employeeAccountDetails", new EmployeeAccountDetails(employeeid));
			}

			if (salarydetails == null && employeeAccountDetail != null) {
				model.addAttribute("employeeAccountDetails", employeeAccountDetail);
			}

			model.addAttribute("paysliperrormessage", true);
			return "Employee";
		}
		session.setAttribute("successmessage", true);
		return "redirect:/employeePayslip?payslip=" + payslipdate + "&empid=" + employeeid;

	}

	@PostMapping("/employeePayslip")
	public ResponseEntity<byte[]> employeePayslip(HttpSession session, @RequestParam("payslip") String payslipdate,
			@RequestParam("empid") String employeeid, Model model) {
		System.out.println("employeePayslip ");
		String[] parts = payslipdate.split(" ");
		String month = parts[0];
		String year = parts[1];
		Salarydetails salarydetails = salarydetailsRepoService.fetchSalaryDetail(month, year, employeeid);

		EmployeeAccountDetails employeeAccountDetail = employeeAccountDetailsRepoService
				.fetchEmployeeAccountDetail(employeeid);

		Employee employee = employeeRepoService.fetchEmployee(employeeid);
		try {
			model.addAttribute("salarydetails", salarydetails);
			model.addAttribute("employeeAccountDetail", employeeAccountDetail);
			model.addAttribute("employeeDetail", employee);
			byte[] logoBytes = Files.readAllBytes(Paths.get("src/main/resources/static/images/nulogic.png"));
			String logoBase64 = Base64.getEncoder().encodeToString(logoBytes);
			model.addAttribute("logoBase64", logoBase64);

			String htmlContent = generateHtmlContent(model);
			byte[] pdfBytes = generatePdfFromHtml(htmlContent);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("inline", employee.getEmpid() + "_Payslip_" + month + "_" + year + "_.pdf");
			return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/employeePayslip")
	public ResponseEntity<byte[]> getemployeePayslip(HttpSession session, @RequestParam("payslip") String payslipdate,
			@RequestParam("empid") String employeeid, Model model) {
		System.out.println("getemployeePayslip ");
		String[] parts = payslipdate.split(" ");
		String month = parts[0];
		String year = parts[1];
		Salarydetails salarydetails = salarydetailsRepoService.fetchSalaryDetail(month, year, employeeid);

		EmployeeAccountDetails employeeAccountDetail = employeeAccountDetailsRepoService
				.fetchEmployeeAccountDetail(employeeid);

		Employee employee = employeeRepoService.fetchEmployee(employeeid);

		try {
			model.addAttribute("salarydetails", salarydetails);
			model.addAttribute("employeeAccountDetail", employeeAccountDetail);
			model.addAttribute("employeeDetail", employee);
			byte[] logoBytes = Files.readAllBytes(Paths.get("src/main/resources/static/images/nulogic.png"));
			String logoBase64 = Base64.getEncoder().encodeToString(logoBytes);
			model.addAttribute("logoBase64", logoBase64);

			String htmlContent = generateHtmlContent(model);
			byte[] pdfBytes = generatePdfFromHtml(htmlContent);
			String filename = employee.getEmpid() + "_Payslip_" + month + "_" + year + "_.pdf";
			sendEmailWithAttachment(pdfBytes, payslipdate,filename,employee.getEmail());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("inline",
					employee.getEmpid() + "_Payslip_" + month + "_" + year + "_.pdf");
			return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	@GetMapping("/loan")
	public String loan() {
		return "loan";
	}

	@GetMapping("/loanrequest")
	public String saveLoanRequest(HttpSession session, @RequestParam("loanamount") BigDecimal loanamount,
			@RequestParam("emistartsfrom") String emistartsfrom, @RequestParam("note") String note,
			@RequestParam("expectedmonth") String expectedmonth,
			@RequestParam("repaymentterms") BigDecimal paymentterms, @RequestParam("emi") BigDecimal emi) {
		System.out.println("emistartsfrom " + emistartsfrom);
		System.out.println(" Employeeid " + session.getAttribute("employeeid"));
		Loan loanRequest = new Loan();
		String employeeid = session.getAttribute("employeeid").toString();
		Basicpay ctcrepo = basicRepo.findByEmpid(employeeid);
		if (ctcrepo != null) {
			loanRequest.setEmpid(ctcrepo.getEmpid());
			loanRequest.setEmailid(ctcrepo.getEmailid());
			loanRequest
					.setBasicpay(ctcrepo.getCtc().multiply(((BigDecimal.valueOf(40)).divide(BigDecimal.valueOf(100))))
							.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP));
			loanRequest.setLoanamount(loanamount);
			loanRequest.setExpectedmonth(formatMonthAndYear(expectedmonth));
			loanRequest.setEmistartsfrom(formatMonthAndYear(emistartsfrom));
			loanRequest.setNote(note);
			loanRequest.setIssuedon(Date.valueOf(LocalDate.now()));
			loanRequest.setLoanrequeststatus("Not Started");
			loanRequest.setLoanstatus("");
			loanRequest.setRemainingbalance(loanamount);
			loanRequest.setRepaymentterms(paymentterms);
			loanRequest.setRequestedby(employeeRepoService.fetchEmployee(employeeid).getName());
			loanRequest.setEmi(emi);
			loanRepo.save(loanRequest);
		}
		return "hello";
	}

	private String generateHtmlContent(Model model) {
		Context context = new Context();
		model.asMap().forEach(context::setVariable);
		String htmlContent = templateEngine.process("Payslip", context);
		htmlContent = htmlContent.replaceAll("<!DOCTYPE[^>]*>", "");
		return htmlContent;
	}

	private byte[] generatePdfFromHtml(String htmlContent) throws IOException, DocumentException {
		System.out.println("HTML Content: " + htmlContent);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(sanitizeHtmlContent(htmlContent));
			renderer.layout();
			renderer.createPDF(outputStream);
			return outputStream.toByteArray();
		}
	}

	private String sanitizeHtmlContent(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		return doc.html().trim();
	}
	

    // Method to send email with attachment
    private void sendEmailWithAttachment(byte[] pdfBytes, String payslipdate, String filename, String email) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email); // Replace with recipient's email address
            helper.setSubject("Payslip for " + payslipdate);
            helper.setText("Please find the attached payslip for " + payslipdate);
            helper.addAttachment(filename, new ByteArrayResource(pdfBytes));

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
	private Date formatMonthAndYear(String expectedmonth) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
			YearMonth yearMonth = YearMonth.parse(expectedmonth, formatter);
			LocalDate firstDayOfMonth = yearMonth.atDay(1);
			Date parseDate = Date.valueOf(firstDayOfMonth);
			System.out.println("month " + Date.valueOf(firstDayOfMonth));
			return parseDate;
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			return null;
		}

	}


}