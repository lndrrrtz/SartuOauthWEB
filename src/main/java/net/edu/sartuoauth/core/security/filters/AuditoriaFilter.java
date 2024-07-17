package net.edu.sartuoauth.core.security.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import net.edu.sartuoauth.core.beans.RegistroAuditoria;
import net.edu.sartuoauth.core.daos.utils.Constantes;
import net.edu.sartuoauth.core.daos.utils.Utilidades;
import net.edu.sartuoauth.core.enums.FlujoAutorizacion;
import net.edu.sartuoauth.core.facades.RegistroAuditoriaFacade;

@Component
public class AuditoriaFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaFilter.class);
	
	private static final String AUDITORIA_REALIZADA = "AUDITORIA_REALIZADA";
	
	@Autowired
	private RegistroAuditoriaFacade registroAuditoriaFacade;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		// configuración personalizada si se desea
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		// Flujo de autorización
		String flujoAutorizacion = httpRequest.getParameter("grant_type");
		
		// Solo registrar en caso de ser flujo de autorización client_credentials
		// El flujo authorization_code se gestiona en LoginAuthenticationProvider
		if (FlujoAutorizacion.CLIENT_CREDENTIALS.getValue().equals(flujoAutorizacion)) {
		
			String requestUri = httpRequest.getRequestURI();
			LOGGER.debug(requestUri);
			Date fecha = new Date();
			String ip =  Utilidades.obtenerIp(httpRequest);
			String clientId = httpRequest.getParameter("client_id");
	
			// scope
			String scope = httpRequest.getParameter("scope");
	
			RegistroAuditoria registroAuditoria = new RegistroAuditoria();
			registroAuditoria.setIp(ip);
			registroAuditoria.setFlujo(flujoAutorizacion);
			registroAuditoria.setClientId(clientId);
			registroAuditoria.setFecha(fecha);
			registroAuditoria.setScope(scope);
	
			LOGGER.debug(registroAuditoria.toString());
	
			chain.doFilter(request, response);
	
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
	
				if (authentication != null && authentication.isAuthenticated()) {
	
					if (FlujoAutorizacion.AUTHORIZATION_CODE.getValue().equals(flujoAutorizacion)) {
						registroAuditoria.setIdUsuario(this.getUsername());
					}
					
					registroAuditoria.setResultado(Constantes.OK);
				} else {
					registroAuditoria.setResultado(Constantes.ERROR);
				}
			}
	
			// Verifica si la auditoría ya se ha realizado para esta solicitud
			if (httpRequest.getAttribute(AUDITORIA_REALIZADA) == null) {
				// registrar la auditoría
				registroAuditoriaFacade.registrarAcceso(registroAuditoria);
	
				// Marca la auditoría como realizada
				httpRequest.setAttribute(AUDITORIA_REALIZADA, Boolean.TRUE);
			}
			
			LOGGER.info("Estado de la respuesta Status: {}", httpResponse.getStatus());
		}
		else {
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy() {
		// Cleanup logic if needed
	}
	
	private String getUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		} else {
			return principal.toString();
		}
	}
	
}
