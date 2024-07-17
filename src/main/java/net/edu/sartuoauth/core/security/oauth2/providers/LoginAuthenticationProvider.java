package net.edu.sartuoauth.core.security.oauth2.providers;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import net.edu.sartuoauth.core.beans.RegistroAuditoria;
import net.edu.sartuoauth.core.beans.Usuario;
import net.edu.sartuoauth.core.daos.utils.Constantes;
import net.edu.sartuoauth.core.enums.FlujoAutorizacion;
import net.edu.sartuoauth.core.facades.RegistroAuditoriaFacade;
import net.edu.sartuoauth.core.facades.UsuarioFacade;
import net.edu.sartuoauth.core.security.oauth2.services.OauthUserDetailsService;

public class LoginAuthenticationProvider extends DaoAuthenticationProvider {
	
	@Autowired
	private HttpSession httpSession;
	
	@Autowired
	private OauthUserDetailsService usernameUserDetailsService;
	
	@Autowired
	private RegistroAuditoriaFacade registroAuditoriaFacade;
	
	@Autowired
	private UsuarioFacade usuarioFacade;
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		
		String idUsuario = auth.getName();
		String contrasena = (String) auth.getCredentials();
		
		Usuario usuario = usuarioFacade.leerUsuario(idUsuario);
		
		Date fecha = new Date();
		
		// Obtener client_id
		String clientId = (String) httpSession.getAttribute(Constantes.CLIENT_ID);
		
		// Obtener scope
		String scope = (String)httpSession.getAttribute(Constantes.SCOPE);
		
		// Rellenar los datos del registro
		RegistroAuditoria registroAuditoria = new RegistroAuditoria();
		registroAuditoria.setIdUsuario(idUsuario);
		registroAuditoria.setFlujo(FlujoAutorizacion.AUTHORIZATION_CODE.getValue());
		registroAuditoria.setFecha(fecha);
		registroAuditoria.setClientId(clientId);
		registroAuditoria.setScope(scope);
		
		// Comprobar si usuario existe
		if(usuario == null) {
			registroAuditoria.setResultado("Usuario no existe");
			
			// Registrar en log de acceso
			registroAuditoriaFacade.registrarAcceso(registroAuditoria);
			throw new BadCredentialsException("Credenciales incorrectas");
//			throw new BadCredentialsException("El usuario no existe");
		}
		else if(!StringUtils.trimToEmpty(contrasena).equals(StringUtils.trimToEmpty(usuario.getContrasena()))) {
			registroAuditoria.setResultado("Credenciales incorrectas");
			
			// Registrar en log de acceso
			registroAuditoriaFacade.registrarAcceso(registroAuditoria);
			throw new BadCredentialsException("Credenciales incorrectas");
			//throw new BadCredentialsException("Contraseña incorrecta");
		}
		
		// Si el proceso de autenticación ha ido bien, registrar acceso
		registroAuditoria.setResultado(Constantes.OK);
		registroAuditoriaFacade.registrarAcceso(registroAuditoria);
		
		// Guardar autenticación en sesión
		usernameUserDetailsService = (OauthUserDetailsService) getUserDetailsService();
		UserDetails userDetails = usernameUserDetailsService.loadUserByUsername(idUsuario);
		return new UsernamePasswordAuthenticationToken(userDetails, auth.getCredentials(), userDetails.getAuthorities());
	}
	
}
