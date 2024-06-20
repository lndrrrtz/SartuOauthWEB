package net.edu.sartuoauth.core.security.web.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.edu.sartuoauth.core.security.web.beans.LoginForm;

//@SessionAttributes(types={LoginForm.class}) 
@Controller
@RequestMapping("/oauth")
public class OauthController {

	private static final String REDIRECT_HOME = "redirect:/oauth/login";
	
	private static final String LOGIN_TILES = "login";

	private static final String FAILURE_MESSAGE = "failureMessage";
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String inicioPrueba(Model model) {
		return REDIRECT_HOME;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String inicio(Model model) {
		model.addAttribute(new LoginForm());
		return LOGIN_TILES;
	}

	@RequestMapping(value = "/sesionCerrada", method = RequestMethod.GET)
	public String permisos(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute(FAILURE_MESSAGE, "errors.sesion.cerrada");
		return REDIRECT_HOME;
	}
	
	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(@RequestParam(required=false) String mensajeError,
			RedirectAttributes redirectAttributes) {
	
		if (StringUtils.isNotBlank(mensajeError)) {
			redirectAttributes.addFlashAttribute(FAILURE_MESSAGE, mensajeError);
		}
		
		return REDIRECT_HOME;
	}

}

