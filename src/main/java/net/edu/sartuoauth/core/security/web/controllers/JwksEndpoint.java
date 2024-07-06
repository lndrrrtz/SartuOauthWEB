package net.edu.sartuoauth.core.security.web.controllers;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Basado en especificación JSON Web Key (JWK): https://datatracker.ietf.org/doc/html/rfc7517
 */
@Controller
public class JwksEndpoint {
	
	@Autowired
	private RSAPublicKey rsaPublicKey;

	@Value("${jwt.keyId}")
	private String keyId;

	@RequestMapping("/.well-known/jwks.json")
	@ResponseBody
	public Map<String, Object> getJwks() {
		Map<String, Object> jwk = new HashMap<>();
		jwk.put("kty", "RSA");
		jwk.put("kid", keyId);
		jwk.put("use", "sig");
		jwk.put("alg", "RS256");
		jwk.put("n", Base64.getUrlEncoder().encodeToString(rsaPublicKey.getModulus().toByteArray()));
		jwk.put("e", Base64.getUrlEncoder().encodeToString(rsaPublicKey.getPublicExponent().toByteArray()));

		return Collections.singletonMap("keys", Collections.singletonList(jwk));
	}
}
