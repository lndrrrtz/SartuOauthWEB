package net.edu.sartuoauth.core;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.filter.CorsFilter;

import net.edu.sartuoauth.core.daos.impl.OauthAuthorizationCodeServices;
import net.edu.sartuoauth.core.daos.impl.OauthJdbcTokenStore;
import net.edu.sartuoauth.core.facades.ClientDetailsServiceFacade;
import net.edu.sartuoauth.core.security.oauth2.OauthTokenEnhancer;
import net.edu.sartuoauth.core.security.oauth2.pkce.PkceAuthorizationCodeTokenGranter;

@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class OauthAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OauthAuthorizationServerConfig.class);
	
	private static final String PERMIT_ALL = "permitAll()";

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JndiObjectFactoryBean jndiObjectFactoryBean;
	
	@Autowired
	private ClientDetailsServiceFacade clientDetailsService;

	@Autowired
	private CorsFilter corsFilter;
	
//	@Value("${jwt.signing.key}")
//	private String signingKey;
	
	@Value("${jwt.privateKey}")
	private Resource privateKeyResource;
	
	@Value("${jwt.publicKey}")
	private Resource publicKeyResource;
	
	@Bean
	public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
		return new OAuth2AccessDeniedHandler();
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		
//		security
//			.tokenKeyAccess("permitAll()")
//			.checkTokenAccess("isAuthenticated()")
//			.allowFormAuthenticationForClients();
		security.allowFormAuthenticationForClients()
			.tokenKeyAccess("permitAll()")
			.checkTokenAccess("isAuthenticated()");
//		security.tokenKeyAccess(PERMIT_ALL).checkTokenAccess(PERMIT_ALL);
		
		security.addTokenEndpointAuthenticationFilter(corsFilter);
	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		endpoints
			.tokenStore(tokenStore())
			.tokenEnhancer(tokenEnhancerChain)
			.authenticationManager(authenticationManager)
//			.authorizationCodeServices(authorizationCodeServices);
		
//		endpoints
		.authenticationManager(authenticationManager)
			.authorizationCodeServices(oauthAuthorizationCodeService())
			.tokenGranter(tokenGranter(endpoints));
		
//		endpoints
//		.authenticationManager(authenticationManager)
//			.authorizationCodeServices(new PkceAuthorizationCodeServices(endpoints.getClientDetailsService(), encoder()))
//			.tokenGranter(tokenGranter(endpoints));
	}

	private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
		List<TokenGranter> granters = new ArrayList<>();

		AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
		ClientDetailsService clientDetailsService = endpoints.getClientDetailsService();
		OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();

		granters.add(new RefreshTokenGranter(tokenServices, clientDetailsService, requestFactory));
		granters.add(new ImplicitTokenGranter(tokenServices, clientDetailsService, requestFactory));
		granters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, requestFactory));
		granters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetailsService, requestFactory));
		granters.add(new PkceAuthorizationCodeTokenGranter(tokenServices, oauthAuthorizationCodeService(), clientDetailsService, requestFactory));

		return new CompositeTokenGranter(granters);
	}
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new OauthTokenEnhancer();
	}

	@Bean
	public TokenStore tokenStore() {
		return new OauthJdbcTokenStore((DataSource)jndiObjectFactoryBean.getObject());
	}
	
	@Bean
	public OauthAuthorizationCodeServices oauthAuthorizationCodeService() {
		return new OauthAuthorizationCodeServices((DataSource)jndiObjectFactoryBean.getObject());
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		try {
			converter.setSigner(rsaSigner());
		} catch (Exception e) {
			LOGGER.debug("Error al asignar verificador a JWTAccessTokenConverter", e);
		}
//		converter.setSigningKey(signingKey);
		return converter;
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setSupportRefreshToken(Boolean.TRUE);
		tokenServices.setTokenStore(tokenStore());
		tokenServices.setClientDetailsService(clientDetailsService);
		return tokenServices;
	}
	
	@Bean
	public JdbcAuthorizationCodeServices authorizationCodeService() {
		return new OauthAuthorizationCodeServices((DataSource)jndiObjectFactoryBean.getObject());
	}

//	@Bean
//	public RsaSigner rsaSigner() throws IOException {
//		byte[] privateKeyBytes = Files.readAllBytes(privateKeyResource.getFile().toPath());
//		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
//		KeyFactory keyFactory;
//		PrivateKey privateKey = null;
//		
//		try {
//			keyFactory = KeyFactory.getInstance("RSA");
//			privateKey = keyFactory.generatePrivate(spec);
//		} catch (Exception e) {
//			LOGGER.error("Error al obtener privateKey para firma de token", e);
//		}
//		return new RsaSigner((RSAPrivateKey)privateKey);
//	}
	
	@Bean
	public RsaSigner rsaSigner() throws Exception {
		String privateKeyPEM = new String(Files.readAllBytes(privateKeyResource.getFile().toPath()));
		privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
									.replace("-----END PRIVATE KEY-----", "")
									.replaceAll("\\s", "");
		byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return new RsaSigner((RSAPrivateKey) privateKey);
	}
	
	@Bean
	public RSAPublicKey rsaPublicKey() throws Exception {
		String publicKeyPEM = new String(Files.readAllBytes(publicKeyResource.getFile().toPath()));
		publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
									.replace("-----END PUBLIC KEY-----", "")
									.replaceAll("\\s", "");
		byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}
}
