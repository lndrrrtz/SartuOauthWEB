package net.edu.sartuoauth.core.security.filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import net.edu.sartuoauth.core.security.authentication.BearerAuthenticationToken;

public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(BearerTokenAuthenticationFilter.class);
	
	private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);
	
	private static final String BEARER = "bearer";
	private static final String TOKEN = "token";
	
	private AntPathMatcher pathMatcher= new AntPathMatcher();
	
	private AuthenticationManager authenticationManager;

	private String[] includedUrls;
	
//	@Autowired
//	private ClientDetailsServiceFacade clientDetailsServiceFacade;
	
	
	public BearerTokenAuthenticationFilter(AuthenticationManager authenticationManager, String... includedUrls) {
		Assert.notNull(authenticationManager, "authenticationManager is required");
		this.authenticationManager = authenticationManager;
		this.includedUrls = includedUrls;
		this.pathMatcher = new AntPathMatcher();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if (authenticationIsRequired() && StringUtils.startsWithIgnoreCase(authorization, BEARER)) {
			
			try {
			
				Matcher matcher = authorizationPattern.matcher(authorization);
				if (!matcher.matches()) {
					throw new InvalidTokenException("Token malformado");
				}
				
				String token = matcher.group(TOKEN);
				
				if (token == null) {
					filterChain.doFilter(request, response);
					return;
				}
				
				BearerAuthenticationToken bearerAuthenticationToken = new BearerAuthenticationToken(token);
				Authentication authenticationResult = this.authenticationManager.authenticate(bearerAuthenticationToken);
				
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				context.setAuthentication(authenticationResult);
				SecurityContextHolder.setContext(context);
			
			} catch (Exception e) {
			
				LOGGER.debug("Solicitud de autenticación fallida. Token: {}", authorization, e);
				
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

