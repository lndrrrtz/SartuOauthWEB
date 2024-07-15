package net.edu.sartuoauth.core.security.oauth2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import net.edu.sartuoauth.core.beans.UsuarioAutenticado;

/**
 * Permite agregar información adicional a un token Oauth 2
 */
public class OauthTokenEnhancer implements TokenEnhancer {
	
	/**
	 * Sobrescribe el método 'enhance' para agregar información adicional al token
	 */
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		// Verifica si el principal de la autenticación es una instancia de UsuarioAutenticado
		if (authentication.getPrincipal() instanceof UsuarioAutenticado) {

			// Obtener el usuario autenticado desde el principal de la autenticación
			UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) authentication.getPrincipal();

			// Crear un mapa para almacenar la información adicional que se agregará al token
			Map<String, Object> additionalInfo = new HashMap<>();

			// Agregar nuevos claims personalizados al mapa de información adicional
			additionalInfo.put("id", usuarioAutenticado.getId());
			additionalInfo.put("nombre", usuarioAutenticado.getNombre());
			additionalInfo.put("dni", usuarioAutenticado.getDni());
			additionalInfo.put("email", usuarioAutenticado.getEmail());

			// Añadir los claims personalizados al token
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		}

		// Devolver el token
		return accessToken;
	}
}
