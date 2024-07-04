package net.edu.sartuoauth.core.daos.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.edu.sartuoauth.core.daos.ClientDetailsServiceDao;

@Repository
public class ClientDetailsServiceDaoImpl implements ClientDetailsServiceDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDetailsServiceDaoImpl.class);

	private static final String CLIENT_ID = "clientid";
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private ObjectMapper objectMapper;
	
	private static final String LOADCLIENTBYCLIENTID_SQL = "SELECT CLIENTID, CLIENTSECRET, RESOURCEIDS, SCOPE, AUTHORIZEDGRANTTYPES, REGISTEREDREDIRECTURIS, AUTHORITIES, ACCESSTOKENVALIDITYSECONDS, REFRESHTOKENVALIDITYSECONDS, ADDITIONALINFORMATION, AUTOAPPROVESCOPES, DESCRIPCION FROM " + Tablas.OAUTHCLIENT + " WHERE CLIENTID = :" + CLIENT_ID;
	
	private RowMapper<ClientDetails> rowMapper = new ClientDetailsRowMapper();

	private class ClientDetailsRowMapper implements RowMapper<ClientDetails> {
		
		@Override
		public ClientDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			BaseClientDetails details = new BaseClientDetails(
				rs.getString("CLIENTID"),
				rs.getString("RESOURCEIDS"),
				rs.getString("SCOPE"),
				rs.getString("AUTHORIZEDGRANTTYPES"),
				rs.getString("AUTHORITIES"),
				rs.getString("REGISTEREDREDIRECTURIS")
			);

			details.setClientSecret(rs.getString("CLIENTSECRET"));
			
			if (rs.getObject("ACCESSTOKENVALIDITYSECONDS") != null) {
				details.setAccessTokenValiditySeconds(rs.getInt("ACCESSTOKENVALIDITYSECONDS"));
			}
			if (rs.getObject("REFRESHTOKENVALIDITYSECONDS") != null) {
				details.setRefreshTokenValiditySeconds(rs.getInt("REFRESHTOKENVALIDITYSECONDS"));
			}
			
			String additionalInformationJson = rs.getString("ADDITIONALINFORMATION");
			
			if (!StringUtils.isEmpty(additionalInformationJson)) {
				try {
					@SuppressWarnings("unchecked")
					Map<String, Object> additionalInformation = objectMapper.readValue(additionalInformationJson, Map.class);
					details.setAdditionalInformation(additionalInformation);
				}
				catch (Exception e) {
					LOGGER.warn("Could not decode JSON for additional information: " + details, e);
				}
			}
			String scopes = rs.getString("AUTOAPPROVESCOPES");
			if (scopes != null) {
				details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(scopes));
			}
			
			return details;
		}
	}
	
	@Override
	public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
		
		Map<String, String> parametro = Collections.singletonMap(CLIENT_ID, clientId);
		
		try {
			return namedParameterJdbcTemplate.queryForObject(LOADCLIENTBYCLIENTID_SQL, parametro, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			String mensajeError = String.format("El cliente de Oauth %s no existe", clientId);
			LOGGER.error(mensajeError, e);
			throw new InvalidClientException(mensajeError);
		}
	}
}

