package net.edu.sartuoauth.core.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.oauth2.provider.ClientDetails;

public class ClientAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private String principal;
	private String credentials;

	public ClientAuthenticationToken(String principal, String credentials, ClientDetails clientDetails) {
		super(clientDetails.getAuthorities());
		this.principal = principal;
		this.credentials = credentials;
		super.setDetails(clientDetails);
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public String getPrincipal() {
		return this.principal;
	}

}

