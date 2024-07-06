package net.edu.sartuoauth.core.security.oauth2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.edu.sartuoauth.core.beans.UsuarioAutenticado;


public class OauthTokenEnhancer implements TokenEnhancer {
	
	@Autowired
	private ObjectMapper objectMapper;

	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
//		Map<String, Object> additionalInfo = new HashMap<>();

		if (authentication.getPrincipal() instanceof UsuarioAutenticado) {
			
			// Aquí se pueden definir nuevos claims
			
			UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) authentication.getPrincipal();

			Map<String, Object> additionalInfo = new HashMap<>();
	        
	        // Agregar nuevos claims personalizados
//			Map<String, Object> additionalInfo = objectMapper.convertValue(usuarioAutenticado, new TypeReference<Map<String, Object>>() {});
			additionalInfo.put("id", usuarioAutenticado.getId());
			additionalInfo.put("nombre", usuarioAutenticado.getNombre());
	        additionalInfo.put("dni", usuarioAutenticado.getDni());
	        additionalInfo.put("email", usuarioAutenticado.getEmail());
	        

	        // Añadir los claims al token
	        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
	        
			
//			Aplicacion aplicacion = aplicacionFacade.leerAplicacion( authentication.getOAuth2Request().getClientId() );
		}

		//		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		
		return accessToken;
	}
}
