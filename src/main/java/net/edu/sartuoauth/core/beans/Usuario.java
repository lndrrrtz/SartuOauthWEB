
package net.edu.sartuoauth.core.beans;

import java.io.Serializable;

import net.edu.sartuoauth.core.enums.TipoUsuario;

public class Usuario implements Serializable{

	private static final long serialVersionUID = 3268778679107821287L;

	private String id;
	
	private String contrasena;
	
	private String nombre;
	
	private String dni;
	
	private String email;
	
	private TipoUsuario tipo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TipoUsuario getTipo() {
		return tipo;
	}

	public void setTipo(TipoUsuario tipo) {
		this.tipo = tipo;
	}

}
