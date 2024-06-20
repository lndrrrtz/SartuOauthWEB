package net.edu.sartuoauth.core.security.oauths2.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Component;

import net.edu.sartuoauth.core.security.authentication.BearerAuthenticationToken;
import net.edu.sartuoauth.core.security.oauths2.models.UsuarioOauth;

@Component
public class BearerAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthenticationProvider.class);
	
	@Autowired
	private DefaultTokenServices resourceServerTokenServices;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String token = (String)authentication.getPrincipal();
		
		OAuth2AccessToken accessToken;
		OAuth2Authentication oAuth2Authentication;
		
		try {
			accessToken = resourceServerTokenServices.readAccessToken(token);
			
			if (accessToken == null || accessToken.isExpired()) {
				throw new InvalidTokenException("Token has expired");
			}
			
			oAuth2Authentication = resourceServerTokenServices.loadAuthentication(token);
			
		} catch (Exception e) {
			LOGGER.debug("Token {} incorreto", token);
			throw new AccessDeniedException("Token incorrecto");
		}
		
		UsuarioOauth usuarioOauth = new UsuarioOauth(oAuth2Authentication.getOAuth2Request().getClientId(), oAuth2Authentication.getUserAuthentication().getName(), token);
		
		return new UsernamePasswordAuthenticationToken(usuarioOauth, null, authentication.getAuthorities());
		
//		User user = new User(authentication.getName(), "prueba", authentication.getAuthorities());
		
//		return new UsernamePasswordAuthenticationToken(usuarioOauth, null, authentication.getAuthorities());
	}
	
	@Override
	public boolean supports(Class<?> auth) {
		return BearerAuthenticationToken.class.isAssignableFrom(auth);
	}
}
