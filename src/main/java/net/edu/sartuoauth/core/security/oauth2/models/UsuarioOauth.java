package net.edu.sartuoauth.core.security.oauth2.models;

import java.io.Serializable;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class UsuarioOauth implements Serializable {

	private static final long serialVersionUID = 1L;

	private String idUsuario;
	
	private String clientId;
	
	private String token;
	
	private OAuth2Authentication oAuth2Authentication;
	
	public UsuarioOauth() {
		super();
	}

	public UsuarioOauth(String clientId) {
		this.clientId = clientId;
	}

	public UsuarioOauth(String clientId, String idUsuario, String token) {
		this.clientId = clientId;
		this.idUsuario = idUsuario;
		this.token = token;
	}

	public UsuarioOauth(String clientId, String idUsuario, String token, OAuth2Authentication oAuth2Authentication) {
		this.clientId = clientId;
		this.idUsuario = idUsuario;
		this.token = token;
		this.oAuth2Authentication = oAuth2Authentication;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public OAuth2Authentication getoAuth2Authentication() {
		return oAuth2Authentication;
	}

	public void setoAuth2Authentication(OAuth2Authentication oAuth2Authentication) {
		this.oAuth2Authentication = oAuth2Authentication;
	}
}
