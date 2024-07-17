package net.edu.sartuoauth.core.security.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import net.edu.sartuoauth.core.beans.Restriccion;
import net.edu.sartuoauth.core.daos.utils.Utilidades;
import net.edu.sartuoauth.core.enums.AccionRestriccion;
import net.edu.sartuoauth.core.enums.TipoRestriccion;
import net.edu.sartuoauth.core.facades.RestriccionFacade;

public class RestriccionesFilter extends OncePerRequestFilter {
	
	@Autowired
	private RestriccionFacade restriccionFacade;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Obtener lista de IPs bloqueadas
		List<Restriccion> restriccionesIp = restriccionFacade.leerRestricciones(TipoRestriccion.IP);
		
		String ip = Utilidades.obtenerIp(request);
		String redirectUri = request.getParameter("redirect_uri");
		
		// Comprobar si es una IP bloqueada, en caso de serlo, redirigir a página de acceso denegado
		for (Restriccion restriccion : restriccionesIp) {
			
			// Si la acción es comprobar la IP integra
			// Si la acción es comprobar parte de la IP
			if ((AccionRestriccion.EQUALS.equals(restriccion.getAccion()) && ip.equals(restriccion.getDatos())) ||
					(AccionRestriccion.CONTAINS.equals(restriccion.getAccion()) && ip.contains(restriccion.getDatos()))) {
				response.sendRedirect(StringUtils.join(request.getContextPath(), "/accessDenied?redirect_uri=", redirectUri));
				return;
			}
		}

		// Si la IP no está bloqueada, continúa con la cadena de filtros
		filterChain.doFilter(request, response);
	}
}
