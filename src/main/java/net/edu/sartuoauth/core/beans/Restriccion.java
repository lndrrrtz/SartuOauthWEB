
package net.edu.sartuoauth.core.beans;

import java.io.Serializable;

import net.edu.sartuoauth.core.enums.AccionRestriccion;
import net.edu.sartuoauth.core.enums.TipoRestriccion;

public class Restriccion implements Serializable {

	private static final long serialVersionUID = 6139359607623497240L;

	private Integer id;
	
	private TipoRestriccion tipo;
	
	private AccionRestriccion accion;
	
	private String datos;
	
	private String descripcion;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TipoRestriccion getTipo() {
		return tipo;
	}

	public void setTipo(TipoRestriccion tipo) {
		this.tipo = tipo;
	}

	public AccionRestriccion getAccion() {
		return accion;
	}

	public void setAccion(AccionRestriccion accion) {
		this.accion = accion;
	}

	public String getDatos() {
		return datos;
	}

	public void setDatos(String datos) {
		this.datos = datos;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
