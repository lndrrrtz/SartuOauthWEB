package net.edu.sartuoauth.core.security.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

public class OauthLogoutHandler extends SecurityContextLogoutHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(OauthLogoutHandler.class);
	
	@Autowired
	private DefaultTokenServices tokenServices;
	
//	@Autowired
//	private PkceAuthorizationCodeServices pkceAuthorizationCodeServices;
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		
		String authorization = request.getHeader("Authorization");
		
		if (authorization != null && authorization.contains("Bearer")) {
			String tokenValue = authorization.replace("Bearer", "").trim();
		
			try {
				tokenServices.revokeToken(tokenValue);
			}
			catch (Exception e) {
				LOGGER.error("Error al eliminar tokens al hacer logout. Token: {}", tokenValue, e);
			}
		}
		
		super.logout(request, response, authentication);
	}
}
