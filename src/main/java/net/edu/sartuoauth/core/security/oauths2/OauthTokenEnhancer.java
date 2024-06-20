package net.edu.sartuoauth.core.security.oauths2;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import net.edu.sartuoauth.core.beans.UsuarioAutenticado;


public class OauthTokenEnhancer implements TokenEnhancer {
	
//	@Autowired
//	private AplicacionFacade aplicacionFacade;
//	
//	@Autowired
//	private ObjectMapper objectMapper;
//	
//	@Autowired
//	private PerfilFacade perfilFacade;
//	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
//		Map<String, Object> additionalInfo = new HashMap<>();

		if (authentication.getPrincipal() instanceof UsuarioAutenticado) {
			
			// Aquí se pueden definir nuevos claims
			
//			UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) authentication.getPrincipal();

//			Aplicacion aplicacion = aplicacionFacade.leerAplicacion( authentication.getOAuth2Request().getClientId() );
		}

		//		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		
		return accessToken;
	}
}
