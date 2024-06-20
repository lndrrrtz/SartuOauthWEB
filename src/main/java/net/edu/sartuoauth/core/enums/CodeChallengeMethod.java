package net.edu.sartuoauth.core.enums;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import net.edu.sartuoauth.core.exceptions.SartuOauthException;

public enum CodeChallengeMethod {
	
	S256 {
		@Override	
		public String transform(String codeVerifier) {
			
			try {
				byte[] bytes = codeVerifier.getBytes("US-ASCII");
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.update(bytes, 0, bytes.length);
				byte[] digest = messageDigest.digest();
				return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException(e);
			} catch (UnsupportedEncodingException e) {
				throw new SartuOauthException();
			}
		}
	},
	PLAIN {
		@Override
		public String transform(String codeVerifier) {
			return codeVerifier;
		}
	},
	NONE {
		@Override
		public String transform(String codeVerifier) {
			throw new UnsupportedOperationException();
		}
	};

	public abstract String transform(String codeVerifier);
}
