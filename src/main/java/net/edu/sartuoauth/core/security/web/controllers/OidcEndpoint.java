package net.edu.sartuoauth.core.security.web.controllers;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.edu.sartuoauth.core.security.oauth2.models.OIDCDiscovery;

@RestController
public class OidcEndpoint {

	private static final String URL_OAUTH = "http://localhost:9081/SartuOauthWEB/";
	
	@Autowired 
	AuthorizationEndpoint authorizationEndpoint;
	
	//	private final JWKSet jwkSet;
//	private final OIDCDiscovery oidcDiscovery;
//
//	public OIDCEndpoint(OIDCDiscovery oidcDiscovery) {
//		this.oidcDiscovery = oidcDiscovery;
//	}

	@RequestMapping("/.well-known/openid-configuration")
	public ResponseEntity<OIDCDiscovery> openIdDiscovery() {
		OIDCDiscovery oidcDiscovery = new OIDCDiscovery();
		oidcDiscovery.setIssuer(String.join(URL_OAUTH, "tokenid"));
		oidcDiscovery.setTokenEndpoint(String.join(URL_OAUTH, "oauth/token"));
		oidcDiscovery.setTokenKeyEndpoint(String.join(URL_OAUTH, "oauth/token_key"));
		oidcDiscovery.setUserinfoEndpoint(String.join(URL_OAUTH, "oauth/userinfo"));
		oidcDiscovery.setCheckTokenEndpoint(String.join(URL_OAUTH, "oauth/check_token"));
		oidcDiscovery.setRevocationEndpoint(String.join(URL_OAUTH, "oauth/revoke"));
		oidcDiscovery.setAuthorizationEndpoint(String.join(URL_OAUTH, "oauth/authorize"));
		oidcDiscovery.setIntrospectionEndpoint(String.join(URL_OAUTH, "oauth/introspect"));
		oidcDiscovery.setEndSessionEndpoint(String.join(URL_OAUTH, "oauth/logout"));
//		oidcDiscovery.setJwksUri(String.join(URL_OAUTH, "openid/.well-known/jwks.json"));
//		oidcDiscovery.setUserinfoSigningAlgSupported(new HashSet<>(Arrays.asList("RS256")));
//		oidcDiscovery.setIdTokenSigningAlgValuesSupported(new HashSet<>(Arrays.asList("RS256")));
//		oidcDiscovery.setTokenEndpointAuthSigningAlgorithmsSupported(new HashSet<>(Arrays.asList("RS256")));
		oidcDiscovery.setScopesSupported(new HashSet<>(Arrays.asList("openid", "profile", "email", "read", "write")));
//		oidcDiscovery.setSubjectTypesSupported(new HashSet<>(Arrays.asList("public", "pairwise")));
		oidcDiscovery.setResponseTypesSupported(new HashSet<>(Arrays.asList("code", "token", "id_token", "code token", "code id_token", "id_token token", "code id_token token")));
//		oidcDiscovery.setClaimsSupported(new HashSet<>(Arrays.asList("iss", "sub", "iat", "azp", "exp", "scope", "at_hash", "c_hash", "nonce")));
		oidcDiscovery.setClaimsSupported(new HashSet<>(Arrays.asList("sub", "name", "email", "locale")));
		oidcDiscovery.setGrantTypesSupported(new HashSet<>(Arrays.asList("authorization_code", "refresh_token", "client_credentials")));
//		oidcDiscovery.setGrantTypesSupported(new HashSet<>(Arrays.asList("client_secret_basic", "client_secret_post")));
		
		return new ResponseEntity<>(oidcDiscovery, HttpStatus.OK);
	}

}
