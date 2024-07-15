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
 * Endpoint que espone JSON Web Key Set (JWKS).
 * 
 * Este controlador proporciona un endpoint JSON Web Key Set (JWKS) que expone la clave pública
 * en un formato que cumple con la especificación JSON Web Key (JWK) definida en 
 * https://datatracker.ietf.org/doc/html/rfc7517">RFC 7517.
 * Esto permite a los clientes obtener la clave pública y usarla para verificar los JWT firmados por Sartu.
 */
@Controller
public class JwksEndpoint {
	
	private static final String KTY = "kty";
	private static final String KID = "kid";
	private static final String USE = "use";
	private static final String ALG = "alg";
	private static final String N = "n"; 
	private static final String E = "e";
	private static final String RSA = "RSA"; 
	private static final String SIG = "sig"; 
	private static final String RS256 = "RS256";
	private static final String KEYS = "keys"; 
	
	@Autowired
	private RSAPublicKey rsaPublicKey;

	@Value("${jwt.keyId}")
	private String keyId;

	@RequestMapping("/.well-known/jwks.json")
	@ResponseBody
	public Map<String, Object> getJwks() {
		Map<String, Object> jwk = new HashMap<>();
		jwk.put(KTY, RSA);
		jwk.put(KID, keyId);
		jwk.put(USE, SIG);
		jwk.put(ALG, RS256);
		jwk.put(N, Base64.getUrlEncoder().encodeToString(rsaPublicKey.getModulus().toByteArray()));
		jwk.put(E, Base64.getUrlEncoder().encodeToString(rsaPublicKey.getPublicExponent().toByteArray()));

		return Collections.singletonMap(KEYS, Collections.singletonList(jwk));
	}
}
