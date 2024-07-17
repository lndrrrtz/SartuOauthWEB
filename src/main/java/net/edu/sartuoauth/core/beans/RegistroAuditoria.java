
package net.edu.sartuoauth.core.beans;

import java.io.Serializable;
import java.util.Date;

public class RegistroAuditoria implements Serializable {

	private static final long serialVersionUID = 1436597532132530297L;

	private String ip;
	
	private String flujo;
	
	private String clientId;
	
	private String idUsuario;
	
	private String resultado;

	private String scope;
	
	private Date fecha;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFlujo() {
		return flujo;
	}

	public void setFlujo(String flujo) {
		this.flujo = flujo;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	@Override
	public String toString() {
		return String.format("FLUJO: %s, SCOPE: %s, CLIENT ID: %s, USUARIO: %s, RESULTADO: %s", flujo, scope, clientId, idUsuario, resultado);
	}
}
