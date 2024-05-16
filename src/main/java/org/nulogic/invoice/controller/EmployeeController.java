package org.nulogic.invoice.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.nulogic.invoice.model.Employee;
import org.nulogic.invoice.model.EmployeeAccountDetails;
import org.nulogic.invoice.model.Salarydetails;
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

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

//	@GetMapping("/employeepage")
//	public String employeePage(HttpSession session, Model model) {
//		System.out.println("/employee getmapping");
//		Object employee = session.getAttribute("employee");
//		Employee employee1 = employeeRepoService.fetchEmployee(((Employee) employee).getEmpid());
//		model.addAttribute("employee", employee1);
//		return "employee";
//	}

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

}