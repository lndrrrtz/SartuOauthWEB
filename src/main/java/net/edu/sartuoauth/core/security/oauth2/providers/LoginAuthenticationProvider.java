package net.edu.sartuoauth.core.security.oauth2.providers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import net.edu.sartuoauth.core.beans.Usuario;
import net.edu.sartuoauth.core.facades.UsuarioFacade;
import net.edu.sartuoauth.core.security.oauth2.services.OauthUserDetailsService;

public class LoginAuthenticationProvider extends DaoAuthenticationProvider {
	
	@Autowired
	private OauthUserDetailsService usernameUserDetailsService;
	
	@Autowired
	private UsuarioFacade usuarioFacade;
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		
		String idUsuario = auth.getName();
		String contrasena = (String) auth.getCredentials();
		
		Usuario usuario = usuarioFacade.leerUsuario(idUsuario);
		
		// Comprobar si usuario existe
		if(usuario == null) {
			throw new BadCredentialsException("Credenciales incorrectas");
//			throw new BadCredentialsException("El usuario no existe");
		}
		else if(!StringUtils.trimToEmpty(contrasena).equals(StringUtils.trimToEmpty(usuario.getContrasena()))) {
			throw new BadCredentialsException("Credenciales incorrectas");
			//throw new BadCredentialsException("Contraseña incorrecta");
		}
		
		usernameUserDetailsService = (OauthUserDetailsService) getUserDetailsService();
		UserDetails userDetails = usernameUserDetailsService.loadUserByUsername(idUsuario);
		return new UsernamePasswordAuthenticationToken(userDetails, auth.getCredentials(), userDetails.getAuthorities());
	}
}
