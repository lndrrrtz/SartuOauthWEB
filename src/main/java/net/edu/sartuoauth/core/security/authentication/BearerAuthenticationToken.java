package net.edu.sartuoauth.core.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class BearerAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 284062397216200738L;
	
	private String principal;
	private String credentials;

	public BearerAuthenticationToken(String principal) {
		super(null);
		this.principal = principal;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public String getPrincipal() {
		return this.principal;
	}
	
}

