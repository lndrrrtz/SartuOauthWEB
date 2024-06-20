package net.edu.sartuoauth.core.security.web.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.edu.sartuoauth.core.security.oauths2.models.UsuarioOauth;

@RestController
public class IntrospectTokenEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(IntrospectTokenEndpoint.class);
	
	@Autowired
	private DefaultTokenServices tokenServices;

	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	
	@Autowired 
	private TokenStore tokenStore;
	
	@RequestMapping(value = "/oauth/introspect", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> introspect(@RequestParam("token") String token,
			@RequestParam(name = "token_type_hint", required = false) String tokenTypeHint, 
			UsuarioOauth usuarioOauth) {
		
		OAuth2AccessToken accessToken = null;
		Map<String, Object> introspectMap = new HashMap<>();
		OAuth2Authentication oAuth2Authentication = null;
		
		if (StringUtils.isBlank(token)) {
			introspectMap.values().removeIf(Objects::isNull);
			return new ResponseEntity<>(introspectMap, HttpStatus.BAD_REQUEST);
		}

		if (StringUtils.isBlank(tokenTypeHint) || tokenTypeHint.equals("access_token")) {
			
			try {
				accessToken = tokenServices.readAccessToken(token);
				
				if (accessToken != null && !accessToken.isExpired()) {
					oAuth2Authentication = tokenServices.loadAuthentication(accessToken.getValue());
				}
			} 
			catch (Exception e) {
				LOGGER.debug("Access token no existe: {} ", token);
			}
		}
		
		if ((StringUtils.isNotBlank(tokenTypeHint) && tokenTypeHint.equals("refresh_token")) || accessToken == null) {
			
			try {
				DefaultExpiringOAuth2RefreshToken refreshToken = (DefaultExpiringOAuth2RefreshToken)tokenStore.readRefreshToken(token);

				// si el token ha expirado
				if (System.currentTimeMillis() < refreshToken.getExpiration().getTime()) {
					oAuth2Authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
					accessToken = tokenStore.getAccessToken(oAuth2Authentication);
				}
			}
			catch (Exception e) {
				LOGGER.debug("Refresh token no existe: {} ", token);
			}
		}
		
		if (oAuth2Authentication == null) {
			introspectMap.put("active", Boolean.FALSE);
		}
		else {

			introspectMap.put("active", Boolean.TRUE);
			
			Map<String, ?> claims = accessTokenConverter.convertAccessToken(accessToken, oAuth2Authentication);
			introspectMap.put("scope", claims.get("scope").toString());
			introspectMap.put("client_id", claims.get("client_id").toString());
			introspectMap.put("username", claims.get("user_name").toString());
			introspectMap.put("exp", claims.get("exp").toString());
			introspectMap.put("sub", claims.get("user_name").toString());
			introspectMap.put("aud", claims.get("aud").toString());
			introspectMap.put("jti", claims.get("jti").toString());
		}

		introspectMap.values().removeIf(Objects::isNull);
		
		return new ResponseEntity<>(introspectMap, HttpStatus.OK);
	}
}

