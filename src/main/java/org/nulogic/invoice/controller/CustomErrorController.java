package org.nulogic.invoice.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

	private static final String PATH = "/error";

	@GetMapping(PATH)
	public String error() {
		return "errorPage";
	}

	public String getErrorPath() {
		return PATH;
	}
}
