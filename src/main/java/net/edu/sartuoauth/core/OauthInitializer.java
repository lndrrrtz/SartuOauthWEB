package net.edu.sartuoauth.core;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.util.WebUtils;

import net.edu.sartuoauth.core.security.oauths2.OauthMethodSecurityConfig;

public class OauthInitializer extends AbstractAnnotationConfigDispatcherServletInitializer
		implements WebApplicationInitializer {

	@Override
	public void onStartup(final ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		final WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		final DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
		servletContext.addServlet("DispatcherServlet", dispatcherServlet);
		servletContext.setInitParameter(WebUtils.HTML_ESCAPE_CONTEXT_PARAM, "true");
		servletContext.addListener(new RequestContextListener());
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[]{ 
			OauthCore.class,
//			OauthWeb.class,
//			OAuth2ServerConfig.class,
//			OauthResourceServerConfiguration.class,
			OauthAuthorizationServerConfig.class,
			OauthEndpointSecurityConfig.class,
			OauthAuthenticationSecurityConfig.class,
			OauthMethodSecurityConfig.class,
		};
	}
	
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[]{ OauthWeb.class };
	}

	@Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setInitParameter("throwExceptionIfNoHandlerFound", Boolean.TRUE.toString());
	}
	
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		
		final CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
		encodingFilter.setForceEncoding(true);
		encodingFilter.setEncoding("UTF-8");
		
		DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy("springSecurityFilterChain");
		springSecurityFilterChain.setContextAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher");
		
		return new Filter[] { encodingFilter, springSecurityFilterChain };
	}

}