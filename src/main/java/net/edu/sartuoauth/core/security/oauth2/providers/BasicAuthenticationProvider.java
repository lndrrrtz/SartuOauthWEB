package net.edu.sartuoauth.core.security.oauth2.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

import net.edu.sartuoauth.core.security.oauth2.models.UsuarioOauth;

//@Component
public class BasicAuthenticationProvider extends DaoAuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationProvider.class);
	
	@Autowired
	private ClientDetailsService clientDetailsService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String clientId = authentication.getName();
		String clientSecret = (String) authentication.getCredentials();
		
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
		
		if (clientDetails == null) {
			LOGGER.debug("El Cliente {} no existe", clientId);
			throw new BadCredentialsException("Credenciales incorrectas");
		}
		else if (clientDetails != null && clientSecret.equals(clientDetails.getClientSecret())) {

			UsuarioOauth usuarioOauth = new UsuarioOauth(clientId);
			
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(usuarioOauth, null, authentication.getAuthorities());
			usernamePasswordAuthenticationToken.setDetails(clientDetails);
			
			return usernamePasswordAuthenticationToken;
		}
		
		throw new BadCredentialsException("Credenciales incorrectas");
	}

	@Override
	public boolean supports(Class<?> auth) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(auth);
	}
}

