package net.edu.sartuoauth.core.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum FlujoAutorizacion {

	CLIENT_CREDENTIALS("client_credentials"),
	AUTHORIZATION_CODE("authorization_code");
	
	private String value;
	
	private FlujoAutorizacion(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public static FlujoAutorizacion getIdioma(String flujoAutorizacion){
		return lookup.get(flujoAutorizacion);
	}

	private static final Map<String,FlujoAutorizacion> lookup = new HashMap<>();

	static {
		for (FlujoAutorizacion s : EnumSet.allOf(FlujoAutorizacion.class)) {
			lookup.put(s.value, s);
		}
	}
}
