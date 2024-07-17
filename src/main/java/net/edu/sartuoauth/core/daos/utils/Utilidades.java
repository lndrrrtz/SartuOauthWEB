package net.edu.sartuoauth.core.daos.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase con métodos de utilidad
 */
public final class Utilidades {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utilidades.class);
	
	public static String obtenerIp(HttpServletRequest request) {
		
		final String server = request.getServerName();
		
		if ("localhost".equalsIgnoreCase(server)) {
			try {
				return InetAddress.getLocalHost().getHostAddress();
			} catch (final UnknownHostException e) {
				LOGGER.error("Error obteniendo ip", e);
			}
		}
	
		final String header = request.getHeader("X-Forwarded-For");
	
		if (header != null) {
			return header;
		}
	
		return request.getRemoteAddr();
	}
}
