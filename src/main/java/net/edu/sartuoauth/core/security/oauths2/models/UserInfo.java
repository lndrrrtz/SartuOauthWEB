package net.edu.sartuoauth.core.security.oauths2.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import net.edu.sartuoauth.core.beans.Usuario;

@JsonPropertyOrder({ "sub", "name", "given_name", "family_name", "email", "email_verified", "locale", "dni", "centro", "departamento", "numeroEmpleado" })
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo implements Serializable {
	
	private static final long serialVersionUID = 8343812291565948927L;

	private String sub;
	
	private String name;
	
	@JsonProperty("given_name")
	private String givenName;
	
	@JsonProperty("family_name")
	private String familyName;
	
	private String email;
	
	@JsonProperty("email_verified")
	private boolean emailVerified;
	
	private String locale;
	
	private String dni;

	public UserInfo(Usuario usuario) {
		this.sub = usuario.getId();
		this.name = usuario.getNombre();
		this.givenName = usuario.getNombre();
		this.familyName = usuario.getNombre();
		this.email = usuario.getEmail();
		this.emailVerified = Boolean.TRUE;
		this.locale = "es-ES";
		this.dni = usuario.getDni();
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}
}

