package net.edu.sartuoauth.core.daos.impl;

import java.sql.Types;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

public class OauthJdbcTokenStore extends JdbcTokenStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(OauthJdbcTokenStore.class);
	
	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
	
	private final JdbcTemplate jdbcTemplate;
	
	private static final String DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT = "INSERT INTO " + Tablas.ACCESSTOKEN + " (TOKEN_ID, TOKEN, AUTHENTICATION_ID, USER_NAME, CLIENT_ID, AUTHENTICATION, REFRESH_TOKEN) values (?, ?, ?, ?, ?, ?, ?)";

	private static final String DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT = "SELECT TOKEN_ID, TOKEN FROM " + Tablas.ACCESSTOKEN + " WHERE TOKEN_ID = ?";

	private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "SELECT TOKEN_ID, AUTHENTICATION FROM " + Tablas.ACCESSTOKEN + " WHERE TOKEN_ID = ?";

	private static final String DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT = "SELECT TOKEN_ID, TOKEN FROM " + Tablas.ACCESSTOKEN + " WHERE AUTHENTICATION_ID = ?";

	private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT = "SELECT TOKEN_ID, TOKEN FROM " + Tablas.ACCESSTOKEN + " WHERE USER_NAME = ? and CLIENT_ID = ?";

	private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT = "SELECT TOKEN_ID, TOKEN FROM " + Tablas.ACCESSTOKEN + " WHERE USER_NAME = ?";

	private static final String DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT = "SELECT TOKEN_ID, TOKEN FROM " + Tablas.ACCESSTOKEN + " WHERE CLIENT_ID = ?";

	private static final String DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT = "DELETE FROM " + Tablas.ACCESSTOKEN + " WHERE TOKEN_ID = ?";

	private static final String DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT = "DELETE FROM " + Tablas.ACCESSTOKEN + " WHERE REFRESH_TOKEN = ?";

	private static final String DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT = "INSERT into " + Tablas.REFRESHTOKEN + " (TOKEN_ID, TOKEN, AUTHENTICATION) values (?, ?, ?)";

	private static final String DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT = "SELECT TOKEN_ID, TOKEN FROM " + Tablas.REFRESHTOKEN + " WHERE TOKEN_ID = ?";

	private static final String DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "SELECT TOKEN_ID, AUTHENTICATION FROM " + Tablas.REFRESHTOKEN + " WHERE TOKEN_ID = ?";

	private static final String DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT = "DELETE FROM " + Tablas.REFRESHTOKEN + " WHERE TOKEN_ID = ?";
	

	public OauthJdbcTokenStore(DataSource dataSource) {
		super(dataSource);
		setInsertAccessTokenSql(DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT);
		setSelectAccessTokenSql(DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT);
		setSelectAccessTokenAuthenticationSql(DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT);
		setSelectAccessTokenFromAuthenticationSql(DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT);
		setSelectAccessTokensFromUserNameAndClientIdSql(DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT);
		setSelectAccessTokensFromUserNameSql(DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT);
		setSelectAccessTokensFromClientIdSql(DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT);
		setDeleteAccessTokenSql(DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT);
		setInsertRefreshTokenSql(DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT);
		setSelectRefreshTokenSql(DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT);
		setSelectRefreshTokenAuthenticationSql(DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT);
		setDeleteRefreshTokenSql(DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT);
		setDeleteAccessTokenFromRefreshTokenSql(DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * Sobreescrito para que no se produzca este error:
	 * There is a race condition in org.springframework.security.oauth2.provider.token.DefaultTokenServices#refreshAccessToken which results in entries with duplicate authentication_id in the table oauth_access_token (even after 2.0.5). This occurs when two threads access the database at the same time but both still get the authentication for the old refresh-token (tokenStore.readAuthenticationForRefreshToken(refreshToken)).
	 * @see <a href="https://github.com/spring-attic/spring-security-oauth/issues/463#issuecomment-99086491}">Error</a>
	 */
	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		
		String refreshToken = null;
		if (token.getRefreshToken() != null) {
			refreshToken = token.getRefreshToken().getValue();
		}
		
		if (readAccessToken(token.getValue())!=null) {
			removeAccessToken(token.getValue());
		}

		try {
			jdbcTemplate.update(DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT, new Object[] { extractTokenKey(token.getValue()),
					new SqlLobValue(serializeAccessToken(token)), authenticationKeyGenerator.extractKey(authentication),
					authentication.isClientOnly() ? null : authentication.getName(),
					authentication.getOAuth2Request().getClientId(),
					new SqlLobValue(serializeAuthentication(authentication)), extractTokenKey(refreshToken) }, new int[] {
					Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.VARCHAR });
		}
		catch (DuplicateKeyException e) {
			LOGGER.debug("Error guardando AccesToken", e);
		}
	}
}

