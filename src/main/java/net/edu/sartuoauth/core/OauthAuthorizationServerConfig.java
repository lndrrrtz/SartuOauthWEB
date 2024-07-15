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
	
	/**
	 * Sobrescribe la configuración predeterminada de la fuente desde donde se va a obtener información de los clientes
	 * 
	 * @Param clients Contiene configuración de los detalles del cliente
	 * @throws Exception producida al intentar configurar la fuente de los clientes
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
	}

	/**
	 * Configura los endpoints del servidor de autorización.
	 * 
	 * Este método sobrescribe la configuración predeterminada de los endpoints del servidor de autorización para personalizar 
	 * la gestión y almacenado de tokens, la gestión de la autenticación y servicios relacionados con el código de autorización.
	 * 
	 * @param endpoints el configurador de endpoints del servidor de autorización utilizado para definir sus características
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		
		// Permite agregar información adicional a los tokens
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		// Configura los endpoints del servidor de autorización 
		endpoints
			// Configura el almacen de tokens
			.tokenStore(tokenStore())
			// Configura la agregación de información adicional a los tokens
			.tokenEnhancer(tokenEnhancerChain)
			// Configura el administrador de autenticación
			.authenticationManager(authenticationManager)
			// Congigura los servicios relacionados con los códigos de autorización
			.authorizationCodeServices(oauthAuthorizationCodeService())
			// Configura el emisor de tokens
			.tokenGranter(tokenGranter(endpoints));
	}

	/**
	 * Configura el emisor de tokens 
	 * 
	 * Este método configura varios tipos de emisores de tokens que se encargan de la emisión tokens
	 * 
	 * @param endpoints el configurador de endpoints del servidor de autorización
	 * @return {@link CompositeTokenGranter} que gestiona la emisión de tokens
	 */
	private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
		
		// Listado de emisores de tokens
		List<TokenGranter> granters = new ArrayList<>();

		// Obtener servicios y fábricas necesarias para la configuración de los endpoints
		AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
		OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();

		// Emisor de tokens de actualización
		granters.add(new RefreshTokenGranter(tokenServices, clientDetailsService, requestFactory));
		// Emisor de tokens implicitos
		granters.add(new ImplicitTokenGranter(tokenServices, clientDetailsService, requestFactory));
		// Emisor de tokens de credenciales de clientes
		granters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, requestFactory));
		// Emisor de tokens de contraseña del propietario del recurso
		granters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetailsService, requestFactory));
		// Emisor de tokens de código de autorización con PKCE
		granters.add(new PkceAuthorizationCodeTokenGranter(tokenServices, oauthAuthorizationCodeService(), clientDetailsService, requestFactory));

		// Devuelve la cofiguración que gestiona la emisión de token
		return new CompositeTokenGranter(granters);
	}
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new OauthTokenEnhancer();
	}

	/**
	 * Configura la utilización de la base de datos para almacenar los tokens.
	 * 
	 * @return {@link OauthJdbcTokenStore} con la configuración de la base de datos.
	 */
	@Bean
	public TokenStore tokenStore() {
		return new OauthJdbcTokenStore((DataSource)jndiObjectFactoryBean.getObject());
	}
	
	@Bean
	public OauthAuthorizationCodeServices oauthAuthorizationCodeService() {
		return new OauthAuthorizationCodeServices((DataSource)jndiObjectFactoryBean.getObject());
	}
	
	/**
	 * Configura la codificación y decodificación de tokens JWT en el servidor de autorización.
	 * 
	 * Permite la conversión y firmado de los tokens JWT.
	 * 
	 * @return {@link JwtAccessTokenConverter} con la configuración para gestionar los tokens JWT
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		try {
			// Configurar la firma de tokens con una firma RSA
			converter.setSigner(rsaSigner());
		} catch (Exception e) {
			LOGGER.debug("Error al asignar firmador a JWTAccessTokenConverter", e);
		}

		// Devolver la configuración para gestionar los token JWT
		return converter;
	}

	/**
	 * Configura la gestión de los tokens. Proporciona los servicios para tokens predeterminados.
	 * 
	 * @return {@link DefaultTokenServices} con configuración para la gestión de los tokens.
	 */
	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		
		// Configuración que permite utilizar tokens de actualización
		tokenServices.setSupportRefreshToken(Boolean.TRUE);
		// Configura el almacen de tokens
		tokenServices.setTokenStore(tokenStore());
		// Configura la fuente desde donde se va a obtener información de los clientes
		tokenServices.setClientDetailsService(clientDetailsService);
		
		// Devuelve la configuración para la gestión de tokens
		return tokenServices;
	}
	
	/**
	 * Configura la gestión de los códigos de autorización y su almacenado en la base de datos
	 * 
	 * @return {@link OauthAuthorizationCodeServices} con configuración  de la base de datos
	 */
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
