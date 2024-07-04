package net.edu.sartuoauth.core.security.web.beans;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class LoginForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@NotNull
	private String idUsuario;
		
	@NotNull
	private String contrasena;
	
	private String error;

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}	
}

