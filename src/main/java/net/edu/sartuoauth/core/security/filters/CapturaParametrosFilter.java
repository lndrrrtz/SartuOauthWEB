package net.edu.sartuoauth.core.security.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import net.edu.sartuoauth.core.daos.utils.Constantes;

/**
 * Filtro para capturar el parámetros y meterlos en sesión para registrar el acceso posteriormente
 *
 */
public class CapturaParametrosFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Inicialización si es necesario
	}

	@Override
	public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		
		// Obtener el client_id
		String clientId = httpRequest.getParameter(Constantes.CLIENT_ID);
		
		// si client_id existe, añadir a sesión
		if (StringUtils.isNotBlank(clientId)) {
			
			// Almacenar el client_id en la sesión HTTP
			session.setAttribute(Constantes.CLIENT_ID, clientId);
		}
		
		// Obtener scope
		String scope = httpRequest.getParameter("scope");
		
		// si no existe scope en el request, intentar obtenerlo
		if (StringUtils.isNotBlank(scope)) {
			
			// Almacenar el scope en la sesión HTTP
			session.setAttribute(Constantes.SCOPE, scope);
		}
		
		
		// Continuar con el resto de filtros
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// Limpieza si es necesario
	}
}