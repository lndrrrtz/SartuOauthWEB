package net.edu.sartuoauth.core.security.oauth2.providers;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Component;

import net.edu.sartuoauth.core.daos.ClientDetailsServiceDao;
import net.edu.sartuoauth.core.security.authentication.ClientAuthenticationToken;
import net.edu.sartuoauth.core.security.oauth2.models.UsuarioOauth;

@Component
public class PublicClientAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublicClientAuthenticationProvider.class);
	
	@Autowired
	private ClientDetailsServiceDao clientDetailsService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String clientId = (String)authentication.getPrincipal();
		String clientSecret = (String)authentication.getCredentials();
		
		if (StringUtils.isNotBlank(clientSecret)) {
			throw new BadCredentialsException("Credenciales erroneas");
		}
		
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
		
		if (clientDetails == null) {
			LOGGER.debug("El Cliente {} no existe", clientId);
			throw new BadCredentialsException("Credenciales incorrectas");
		}
		else if (!clientDetails.isSecretRequired()) {
			
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			
			UsuarioOauth usuarioOauth = new UsuarioOauth(clientId);
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(usuarioOauth, null, authentication.getAuthorities());
			usernamePasswordAuthenticationToken.setDetails(clientDetails);
			return usernamePasswordAuthenticationToken;
		}
		else {
			LOGGER.debug("Cliente {} sin autorización para ser público", clientId);
			throw new BadCredentialsException("Credenciales incorrectas");
		}
		
	}
	
	@Override
	public boolean supports(Class<?> auth) {
		return ClientAuthenticationToken.class.isAssignableFrom(auth);
	}
}
