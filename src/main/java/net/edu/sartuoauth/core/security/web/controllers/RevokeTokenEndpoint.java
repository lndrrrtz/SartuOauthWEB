package net.edu.sartuoauth.core.security.web.controllers;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.edu.sartuoauth.core.security.oauth2.models.UsuarioOauth;

@RestController
public class RevokeTokenEndpoint /* extends AbstractEndpoint */ {

	private static final Logger LOGGER = LoggerFactory.getLogger(RevokeTokenEndpoint.class);
	
	@Autowired
	private DefaultTokenServices tokenServices;
	
	@Autowired 
	private TokenStore tokenStore;

	@RequestMapping(value = "/oauth/revoke", method = RequestMethod.POST)
	public ResponseEntity<Void> revokeToken(@RequestParam("token") String token, 
			@RequestParam(name = "token_type_hint", required = false) String tokenTypeHint, 
			UsuarioOauth usuarioOauth) {
		
		LOGGER.debug("Revocar token. Acceso al endpoint: usuario: {}, cliente: {}, token: {}",
				usuarioOauth.getIdUsuario(), usuarioOauth.getClientId(), token);
		
		if (!StringUtils.isBlank(token)) {
			
			OAuth2Authentication oAuth2Authentication;
			
			try {
				
				if (StringUtils.isBlank(tokenTypeHint) || tokenTypeHint.equals("access_token")) {
					
					OAuth2AccessToken accessToken = tokenServices.readAccessToken(token);
					
					if (accessToken != null && !accessToken.isExpired()) {
						
						oAuth2Authentication = tokenServices.loadAuthentication(accessToken.getValue());
						
						if (oAuth2Authentication != null && usuarioOauth.getClientId().equals(oAuth2Authentication.getOAuth2Request().getClientId())) {
							tokenServices.revokeToken(token);
						}
					}
				}
				else if (tokenTypeHint.equals("refresh_token")) {
					
					OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(token);
					
					if (refreshToken != null) {
						
						oAuth2Authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
						
						if (oAuth2Authentication != null && usuarioOauth.getClientId().equals(oAuth2Authentication.getOAuth2Request().getClientId())) {
							tokenStore.removeRefreshToken(refreshToken);
						}
					}
					
				}
				
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
		
		return ResponseEntity.ok().build();
	}
}

