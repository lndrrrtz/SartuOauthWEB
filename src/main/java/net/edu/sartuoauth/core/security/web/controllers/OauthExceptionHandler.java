package net.edu.sartuoauth.core.security.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import net.edu.sartuoauth.core.exceptions.SartuOauthException;

@ControllerAdvice
public class OauthExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(OauthExceptionHandler.class);
	
	private static final String URL_REDIRECT_LOGIN= "redirect:/oauth/login";
	
	private static final String FAILURE_MESSAGE = "failureMessage";
	
	protected static final String ERRORS_DEFECTO_APLICACION = "errors.defecto.aplicacion";
	
	@ExceptionHandler(SartuOauthException.class)
	public ModelAndView handleSartuOauthException(SartuOauthException sartuOauthException) {

		LOGGER.error(sartuOauthException.getMessage(), sartuOauthException);

		ModelAndView mav = new ModelAndView(URL_REDIRECT_LOGIN);
		mav.addObject(FAILURE_MESSAGE, sartuOauthException.getMessage());
		return mav;
	}

	@ExceptionHandler({
		InvalidTokenException.class,
		MissingServletRequestParameterException.class
	})
	public ResponseEntity<OAuth2Exception> badRequestException(Exception e) throws Exception {
		LOGGER.error(e.getMessage(), e);
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoHandlerFoundException.class)
	public ModelAndView handleException(NoHandlerFoundException noHandlerFoundException) {
		
		LOGGER.error("Se ha producido un error en la aplicación", noHandlerFoundException);
		
		ModelAndView mav = new ModelAndView(URL_REDIRECT_LOGIN);
		mav.addObject(FAILURE_MESSAGE, "Página no encontrada");
		return mav;
	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception exception){
		
		LOGGER.error("Se ha producido un error en la aplicación", exception);
		
		ModelAndView mav = new ModelAndView(URL_REDIRECT_LOGIN);
		mav.addObject(FAILURE_MESSAGE, "error.aplicacion");
		return mav;
	}
}

