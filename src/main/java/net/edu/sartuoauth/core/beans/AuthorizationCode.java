package net.edu.sartuoauth.core.beans;

import java.io.Serializable;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

import net.edu.sartuoauth.core.enums.CodeChallengeMethod;

public class AuthorizationCode implements Serializable {

	private static final long serialVersionUID = -1666548392012473102L;

	private String code;
	
	private String codechallenge;
	
	private CodeChallengeMethod codeChallengeMethod;

	private OAuth2Authentication authentication;

	public AuthorizationCode() {
		super();
	}

	public AuthorizationCode(String code, OAuth2Authentication authentication) {
		this.code = code;
		this.authentication = authentication;
	}
	
	public AuthorizationCode(String code, String codechallenge, CodeChallengeMethod codeChallengeMethod,
			OAuth2Authentication authentication) {
		this.code = code;
		this.codechallenge = codechallenge;
		this.codeChallengeMethod = codeChallengeMethod;
		this.authentication = authentication;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodechallenge() {
		return codechallenge;
	}

	public void setCodechallenge(String codechallenge) {
		this.codechallenge = codechallenge;
	}

	public CodeChallengeMethod getCodeChallengeMethod() {
		return codeChallengeMethod;
	}

	public void setCodeChallengeMethod(CodeChallengeMethod codeChallengeMethod) {
		this.codeChallengeMethod = codeChallengeMethod;
	}

	public OAuth2Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(OAuth2Authentication authentication) {
		this.authentication = authentication;
	}
}

