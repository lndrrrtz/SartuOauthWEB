package net.edu.sartuoauth.core.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

//@Service
//@Aspect
public class SessionInvalidationOauth2GrantAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionInvalidationOauth2GrantAspect.class);

	private static final String FORWARD_OAUTH_CONFIRM_ACCESS = "forward:/oauth/login";
		
	@AfterReturning(value = "within(org.springframework.security.oauth2.provider.endpoint..*) && @annotation(org.springframework.web.bind.annotation.RequestMapping)", returning = "result")
	public void authorizationAdvice(JoinPoint joinpoint, ModelAndView result) throws Throwable {
	
		// If we're not going to the confirm_access page, it means approval has been skipped due to existing access
		// token or something else and they'll be being sent back to app. Time to end session.
		if (!FORWARD_OAUTH_CONFIRM_ACCESS.equals(result.getViewName())) {
			invalidateSession();
		}
	}
	
	@AfterReturning(value = "within(org.springframework.security.oauth2.provider.endpoint..*) && @annotation(org.springframework.web.bind.annotation.RequestMapping)", returning = "result")
	public void authorizationAdvice(JoinPoint joinpoint, View result) throws Throwable {
		// Anything returning a view and not a ModelView is going to be redirecting outside of the app (I think). 
		// This happens after the authorize approve / deny page with the POST to /oauth/authorize. This is the time
		// to kill the session since they'll be being sent back to the requesting app.
		invalidateSession();
	}
	
	@AfterThrowing(value = "within(org.springframework.security.oauth2.provider.endpoint..*) &&  @annotation(org.springframework.web.bind.annotation.RequestMapping)", throwing = "error")
	public void authorizationErrorAdvice(JoinPoint joinpoint) throws Throwable {
		invalidateSession();
	}

	/*
	@AfterReturning("execution(* net.edu.sartuoauth.web.controllers.RevokeTokenEndpoint.revokeToken(..))")
//	@AfterReturning("within(net.edu.sartuoauth.web.controllers.RevokeTokenEndpoint)")
	public void revokeAdvice() throws Throwable {
		invalidateSession();
	}
	*/
	
	private void invalidateSession() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession(false);
		if (session != null) {
			LOGGER.warn(String.format("As part of OAuth application grant processing, invalidating session for request %s", request.getRequestURI()));
			
			session.invalidate();
			SecurityContextHolder.clearContext();
		}
	}
	
	@Bean
	private SessionInvalidationOauth2GrantAspect sessionInvalidationOauth2GrantAspect() {
		return new SessionInvalidationOauth2GrantAspect();
	}

}
