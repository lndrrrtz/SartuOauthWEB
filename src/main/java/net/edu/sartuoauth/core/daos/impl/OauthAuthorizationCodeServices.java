package net.edu.sartuoauth.core.daos.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;

import net.edu.sartuoauth.core.beans.AuthorizationCode;
import net.edu.sartuoauth.core.daos.ClientDetailsServiceDao;
import net.edu.sartuoauth.core.enums.CodeChallengeMethod;

public class OauthAuthorizationCodeServices extends JdbcAuthorizationCodeServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(OauthAuthorizationCodeServices.class);
	
	private static final RandomValueStringGenerator generator = new RandomValueStringGenerator();

	private static final String CODE = "code";
	private static final String AUTHENTICATION = "authentication";
	private static final String CODECHALLENGE = "codechallenge";
	private static final String CODECHALLENGEMETHOD = "codechallengemethod";
	
	@Autowired
	private ClientDetailsServiceDao clientDetailsService;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final String LEERAUTHORIZATIONCODE_SQL = StringUtils.join("SELECT CODE, CODECHALLENGE, CODECHALLENGEMETHOD, AUTHENTICATION FROM ", Tablas.AUTHORIZATIONCODE, " WHERE CODE = :", CODE);
	
	private static final String INSERTARAUTHORIZATIONCODE_SQL = StringUtils.join("INSERT INTO ", Tablas.AUTHORIZATIONCODE, " (CODE, AUTHENTICATION) VALUES (:", CODE, ", :", AUTHENTICATION, ")");
	
	private static final String INSERTARAUTHORIZATIONCODEPKCE_SQL = StringUtils.join("INSERT INTO ", Tablas.AUTHORIZATIONCODE, " (CODE, CODECHALLENGE, CODECHALLENGEMETHOD,AUTHENTICATION) VALUES (:", CODE, ", :", CODECHALLENGE, ", :", CODECHALLENGEMETHOD, ", :", AUTHENTICATION, ")");

	private static final String ELIMINARAUTHORIZATIONCODE_SQL = StringUtils.join("DELETE FROM ", Tablas.AUTHORIZATIONCODE, " WHERE CODE = :", CODE);

	private static final RowMapper<AuthorizationCode> rowMapper = new RowMapper<AuthorizationCode>() {

		@Override
		public AuthorizationCode mapRow(ResultSet rs, int rowNum) throws SQLException {
			AuthorizationCode authorizationCode = new AuthorizationCode();
			authorizationCode.setCode(rs.getString("CODE"));
			authorizationCode.setAuthentication(SerializationUtils.deserialize(rs.getBytes("AUTHENTICATION")));
			
			String codeChallenge = StringUtils.trim(rs.getString("CODECHALLENGE"));
			
			if (StringUtils.isNotBlank(codeChallenge)) {
				
				authorizationCode.setCodechallenge(codeChallenge);
				
				CodeChallengeMethod codeChallengeMethod;
				try {
					 codeChallengeMethod = CodeChallengeMethod.valueOf(StringUtils.trim(rs.getString("CODECHALLENGEMETHOD")));
				} catch (Exception e) {
					codeChallengeMethod = null;
				}
				
				authorizationCode.setCodeChallengeMethod(codeChallengeMethod);
			}
			
			return authorizationCode;
		}
	};
	
	public OauthAuthorizationCodeServices(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public String createAuthorizationCode(OAuth2Authentication authentication) {
		
		// Crear código de autorización
		String code = generator.generate();
		
		//Obtiene los datos de la autenticación
		AuthorizationCode authorizationCode = this.getAuthentication(code, authentication);
		
		this.insertarAuthorizationCode(authorizationCode);
		
		return code;
	}
	
	/**
	 * Obtiene los datos de la autenticación
	 * 
	 * @param code Code 
	 * @param authentication Datos de autenticación
	 * @return {@link AuthorizationCode} con los datos de la autenticación
	 */
	private AuthorizationCode getAuthentication(String code, OAuth2Authentication authentication) {
		
		Map<String, String> requestParameters = authentication.getOAuth2Request().getRequestParameters();

		if (isPublicClient(requestParameters.get("client_id")) && !requestParameters.containsKey("code_challenge")) {
			throw new InvalidRequestException("Code Challenge obligatorio.");
		}

		if (requestParameters.containsKey("code_challenge")) {
			String codeChallenge = requestParameters.get("code_challenge");
			CodeChallengeMethod codeChallengeMethod = getCodeChallengeMethod(requestParameters);
			return new AuthorizationCode(code, codeChallenge, codeChallengeMethod, authentication);
		}

		return new AuthorizationCode(code, authentication);
	}
	
	private CodeChallengeMethod getCodeChallengeMethod(Map<String, String> requestParameters) {
		try {
			return Optional.ofNullable(requestParameters.get("code_challenge_method")).map(String::toUpperCase)
					.map(CodeChallengeMethod::valueOf).orElse(CodeChallengeMethod.PLAIN);
		} catch (IllegalArgumentException e) {
			throw new InvalidRequestException("Transform algorithm not supported");
		}
	}

	private boolean isPublicClient(String clientId) {
		String clientSecret = clientDetailsService.loadClientByClientId(clientId).getClientSecret();
		return StringUtils.isBlank(clientSecret);
	}

	public OAuth2Authentication consumeAuthorizationCodeAndCodeVerifier(String code, String codeVerifier) {

		AuthorizationCode authorizationCode = this.leerAuthorizationCode(code);
		
		CodeChallengeMethod codeChallengeMethod = authorizationCode.getCodeChallengeMethod();
			
		if (StringUtils.isBlank(authorizationCode.getCodechallenge()) || codeChallengeMethod == CodeChallengeMethod.NONE) {
			return authorizationCode.getAuthentication();
		} else if (codeChallengeMethod.transform(codeVerifier).equals(authorizationCode.getCodechallenge())) {
			return authorizationCode.getAuthentication();
		} else { 
			throw new InvalidGrantException("Invalid code verifier.");
		}
	}
	
	@Override
	public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
		throw new UnsupportedOperationException();
	}

	private int insertarAuthorizationCode(AuthorizationCode authorizationCode) {
		return this.insertarAuthorizationCode(authorizationCode.getCode() , authorizationCode.getCodechallenge(), authorizationCode.getCodeChallengeMethod(), authorizationCode.getAuthentication());
	}
	
	private int insertarAuthorizationCode(String code, String codechallenge, CodeChallengeMethod codeChallengeMethod, OAuth2Authentication authentication) {
		MapSqlParameterSource parametros = new MapSqlParameterSource();
		parametros.addValue(CODE, code);
		parametros.addValue(AUTHENTICATION, SerializationUtils.serialize(authentication), Types.BLOB);
		parametros.addValue(CODECHALLENGE, codechallenge);
		parametros.addValue(CODECHALLENGEMETHOD, codeChallengeMethod, Types.VARCHAR);
		return namedParameterJdbcTemplate.update(INSERTARAUTHORIZATIONCODEPKCE_SQL, parametros);
	}
	
	@Override
	protected void store(String code, OAuth2Authentication authentication) {
		MapSqlParameterSource parametros = new MapSqlParameterSource();
		parametros.addValue(CODE, code);
		parametros.addValue(AUTHENTICATION, SerializationUtils.serialize(authentication), Types.BLOB);
		namedParameterJdbcTemplate.update(INSERTARAUTHORIZATIONCODE_SQL, parametros);
	}
	
	private AuthorizationCode leerAuthorizationCode(String code) {
		
		Map<String, String> parametro = Collections.singletonMap(CODE, code);
		
		try {
			return namedParameterJdbcTemplate.queryForObject(LEERAUTHORIZATIONCODE_SQL, parametro, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			LOGGER.debug("Error al leer authorization code {} para oauth 2.0", code, e);
			return null;
		}
	}
	
	@Override
	public OAuth2Authentication remove(String code) {
		
		AuthorizationCode authorizationCode = this.leerAuthorizationCode(code);
		
		if (authorizationCode != null) {
			Map<String, String> parametro = Collections.singletonMap(CODE, code);
			namedParameterJdbcTemplate.update(ELIMINARAUTHORIZATIONCODE_SQL, parametro);
			return authorizationCode.getAuthentication();
		}
		
		return null;
	}
	
}

