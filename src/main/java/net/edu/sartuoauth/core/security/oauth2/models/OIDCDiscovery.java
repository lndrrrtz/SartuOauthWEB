package net.edu.sartuoauth.core.security.oauth2.models;

import java.io.Serializable;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"issuer","authorization_endpoint", "token_endpoint", "userinfo_endpoint", "scopes_supported", "response_types_supported", "grant_types_supported", "token_key_endpoint", "check_token_endpoint", "introspection_endpoint", "end_session_endpoint", "revocation_endpoint", "subject_types_supported", "claims_supported", "token_endpoint_auth_methods_supported" }) 
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OIDCDiscovery implements Serializable {

	private static final long serialVersionUID = 4261965446164116380L;

	@JsonIgnore
	@Value("${oauth2.openid.discovery.baseUri}")
	private String baseUri;
	
	@Value("${oauth2.openid.discovery.issuer}")
	private String issuer;
	
	@Value("oauth2.openid.discovery.authorizationEndpoint")
	@JsonProperty("authorization_endpoint")
	private String authorizationEndpoint;
	
	@Value("oauth2.openid.discovery.tokenEndpoint")
	@JsonProperty("token_endpoint")
	private String tokenEndpoint;
	
	@Value("oauth2.openid.discovery.userinfoEndpoint")
	@JsonProperty("userinfo_endpoint")
	private String userinfoEndpoint;
	
//	@Value("oauth2.openid.discovery.jwksUri")
//	@JsonProperty("jwks_uri")
//	private String jwksUri;

	@Value("oauth2.openid.discovery.scopesSupported")
	@JsonProperty("scopes_supported")
	private Set<String> scopesSupported;
	
	@Value("oauth2.openid.discovery.responseTypesSupported")
	@JsonProperty("response_types_supported")
	private Set<String> responseTypesSupported;
	
	@Value("oauth2.openid.discovery.grantTypesSupported")
	@JsonProperty("grant_types_supported")
	private Set<String> grantTypesSupported;
	
	@Value("oauth2.openid.discovery.tokenKeyEndpoint")
	@JsonProperty("token_key_endpoint")
	private String tokenKeyEndpoint;
	
	@Value("oauth2.openid.discovery.checkTokenEndpoint")
	@JsonProperty("check_token_endpoint")
	private String checkTokenEndpoint;
	
	
	@Value("oauth2.openid.discovery.introspectionEndpoint")
	@JsonProperty("introspection_endpoint")
	private String introspectionEndpoint;
	
	@Value("oauth2.openid.discovery.revocationEndpoint")
	@JsonProperty("revocation_endpoint")
	private String revocationEndpoint;

//	@Value("oauth2.openid.discovery.userinfoSigningAlgSupported")
//	@JsonProperty("userinfo_signing_alg_values_supported")
//	private Set<String> userinfoSigningAlgSupported;
	
//	@Value("oauth2.openid.discovery.idTokenSigningAlgValuesSupported")
//	@JsonProperty("id_token_signing_alg_values_supported")
//	private Set<String> idTokenSigningAlgValuesSupported;
	
//	@Value("oauth2.openid.discovery.tokenEndpointAuthSigningAlgorithmsSupported")
//	@JsonProperty("token_endpoint_auth_signing_alg_values_supported")
//	private Set<String> tokenEndpointAuthSigningAlgorithmsSupported;

	
	@Value("oauth2.openid.discovery.subjectTypesSupported")
	@JsonProperty("subject_types_supported")
	private Set<String> subjectTypesSupported;
	
	
	@Value("oauth2.openid.discovery.claimsSupported")
	@JsonProperty("claims_supported")
	private Set<String> claimsSupported;
	
	
	@Value("oauth2.openid.discovery.tokenEndpointAuthMethodsSupported")
	@JsonProperty("token_endpoint_auth_methods_supported")
	private Set<String> tokenEndpointAuthMethodsSupported;

	@Value("oauth2.openid.discovery.endSessionEndpoint")
	@JsonProperty("end_session_endpoint")
	private String endSessionEndpoint;
	
	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	public void setTokenEndpoint(String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}

	public String getTokenKeyEndpoint() {
		return tokenKeyEndpoint;
	}

	public void setTokenKeyEndpoint(String tokenKeyEndpoint) {
		this.tokenKeyEndpoint = tokenKeyEndpoint;
	}

	public String getAuthorizationEndpoint() {
		return authorizationEndpoint;
	}

	public void setAuthorizationEndpoint(String authorizationEndpoint) {
		this.authorizationEndpoint = authorizationEndpoint;
	}

	public String getCheckTokenEndpoint() {
		return checkTokenEndpoint;
	}

	public void setCheckTokenEndpoint(String checkTokenEndpoint) {
		this.checkTokenEndpoint = checkTokenEndpoint;
	}

	public String getUserinfoEndpoint() {
		return userinfoEndpoint;
	}

	public void setUserinfoEndpoint(String userinfoEndpoint) {
		this.userinfoEndpoint = userinfoEndpoint;
	}

	public String getIntrospectionEndpoint() {
		return introspectionEndpoint;
	}

	public void setIntrospectionEndpoint(String introspectionEndpoint) {
		this.introspectionEndpoint = introspectionEndpoint;
	}

	public String getRevocationEndpoint() {
		return revocationEndpoint;
	}

	public void setRevocationEndpoint(String revocationEndpoint) {
		this.revocationEndpoint = revocationEndpoint;
	}

	public Set<String> getScopesSupported() {
		return scopesSupported;
	}

	public void setScopesSupported(Set<String> scopesSupported) {
		this.scopesSupported = scopesSupported;
	}

	public Set<String> getSubjectTypesSupported() {
		return subjectTypesSupported;
	}

	public void setSubjectTypesSupported(Set<String> subjectTypesSupported) {
		this.subjectTypesSupported = subjectTypesSupported;
	}

	public Set<String> getResponseTypesSupported() {
		return responseTypesSupported;
	}

	public void setResponseTypesSupported(Set<String> responseTypesSupported) {
		this.responseTypesSupported = responseTypesSupported;
	}

	public Set<String> getClaimsSupported() {
		return claimsSupported;
	}

	public void setClaimsSupported(Set<String> claimsSupported) {
		this.claimsSupported = claimsSupported;
	}

	public Set<String> getGrantTypesSupported() {
		return grantTypesSupported;
	}

	public void setGrantTypesSupported(Set<String> grantTypesSupported) {
		this.grantTypesSupported = grantTypesSupported;
	}

	public Set<String> getTokenEndpointAuthMethodsSupported() {
		return tokenEndpointAuthMethodsSupported;
	}

	public void setTokenEndpointAuthMethodsSupported(Set<String> tokenEndpointAuthMethodsSupported) {
		this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
	}

	public String getEndSessionEndpoint() {
		return endSessionEndpoint;
	}

	public void setEndSessionEndpoint(String endSessionEndpoint) {
		this.endSessionEndpoint = endSessionEndpoint;
	}
}
