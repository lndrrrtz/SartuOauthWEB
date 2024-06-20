package net.edu.sartuoauth.core.security.filters;

import java.io.IOException;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import net.edu.sartuoauth.core.facades.ClientDetailsServiceFacade;
import net.edu.sartuoauth.core.security.authentication.ClientAuthenticationToken;

public class PublicClientAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublicClientAuthenticationFilter.class);

	private String[] includedUrls;
	
	private AntPathMatcher pathMatcher= new AntPathMatcher();

	private AuthenticationManager authenticationManager; 
	
	@Autowired
	private ClientDetailsServiceFacade clientDetailsServiceFacade;
	
	public PublicClientAuthenticationFilter(AuthenticationManager authenticationManager, String... includedUrls) {
		Assert.notNull(authenticationManager, "authenticationManager is required");
		this.authenticationManager = authenticationManager;
		this.includedUrls = includedUrls;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		
		String clientId = request.getParameter("client_id");
		String clientSecret = request.getParameter("client_secret");
		
		if (authenticationIsRequired() && StringUtils.isNotBlank(clientId)) {
			
			try {
				
				ClientDetails clientDetails = clientDetailsServiceFacade.loadClientByClientId(clientId);
				
				if (!clientDetails.isSecretRequired()) {
					
					ClientAuthenticationToken clientAuthenticationToken = new ClientAuthenticationToken(clientId, clientSecret, clientDetails);
					Authentication authenticationResult = authenticationManager.authenticate(clientAuthenticationToken);
				
					SecurityContext context = SecurityContextHolder.createEmptyContext();
					context.setAuthentication(authenticationResult);
					SecurityContextHolder.setContext(context);
				}
				else {
					LOGGER.debug("Cliente {} sin autorización para ser público", clientId);
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
			} 
			
			catch (Exception e) {
				LOGGER.debug(e.getMessage(), e);
				
				SecurityContextHolder.clearContext();
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		
		filterChain.doFilter(request, response);
	}
	
	/**
	 * Solo permite los includedUrls
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String url = request.getPathInfo();
		return Stream.of(includedUrls).noneMatch(includedUrl -> pathMatcher.match(includedUrl, url));
	}
	
	protected boolean authenticationIsRequired() {
		
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
		
		if (existingAuth == null || !existingAuth.isAuthenticated()) {
			return true;
		}
		
		return (existingAuth instanceof AnonymousAuthenticationToken);
	}
	
}

