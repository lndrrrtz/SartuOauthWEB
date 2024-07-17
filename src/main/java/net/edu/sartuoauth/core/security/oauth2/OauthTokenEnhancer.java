package net.edu.sartuoauth.core.security.oauth2;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
			
			// OpenId specification: {@link https://openid.net/specs/openid-connect-core-1_0.html} 
			//	-> In addition to the response parameters specified by OAuth 2.0, the following parameters 
			//		MUST be included in the response: id_token
			// OAuth 2.0 specification: {@link https://www.rfc-editor.org/rfc/rfc6749.txt}
			additionalInfo.put("id_token", this.extractTokenKey(accessToken.getValue()));
			
			// Añadir los claims personalizados al token
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		}

		// Devolver el token
		return accessToken;
	}
	
	/**
	 * Se genera id_token igual utilizando el mismo método que JwtTokenStore
	 * 
	 * @param value Valor del token
	 * @return id_token
	 */
	protected String extractTokenKey(String value) {
		if (value == null) {
			return null;
		}
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}
}
