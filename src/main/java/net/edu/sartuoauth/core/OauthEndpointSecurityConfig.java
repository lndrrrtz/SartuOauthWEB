package net.edu.sartuoauth.core;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;

import net.edu.sartuoauth.core.facades.ClientDetailsServiceFacade;
import net.edu.sartuoauth.core.security.filters.BearerTokenAuthenticationFilter;
import net.edu.sartuoauth.core.security.filters.PublicClientAuthenticationFilter;
import net.edu.sartuoauth.core.security.oauth2.providers.BasicAuthenticationProvider;
import net.edu.sartuoauth.core.security.oauth2.providers.BearerAuthenticationProvider;
import net.edu.sartuoauth.core.security.oauth2.providers.PublicClientAuthenticationProvider;
import net.edu.sartuoauth.core.security.oauth2.services.OauthUserDetailsService;

/**
 * Configura la autenticación y los filtros de seguridad para la aplicación.
 * 
 * Personaliza la configuración de seguridad de Spring Security.
 */
@Order(1)
@Configuration
@EnableWebSecurity
public class OauthEndpointSecurityConfig extends WebSecurityConfigurerAdapter {

//		@Autowired
//		private BasicAuthenticationProvider basicAuthenticationProvider;

	@Autowired
	private BearerAuthenticationProvider bearerAuthenticationProvider;

	@Autowired
	private ClientDetailsServiceFacade clientDetailsService;

	@Autowired
	private PublicClientAuthenticationProvider publicClientAuthenticationProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.requestMatchers()
				.antMatchers("/oauth/revoke", "/oauth/userinfo", "/oauth/introspect", ".well-known/openid-configuration")
			.and()
		
			.addFilterBefore(corsFilter(), ChannelProcessingFilter.class)
			.addFilterBefore(getCharacterEncodingFilter(), CsrfFilter.class)
			.addFilterAfter(bearerTokenAutenticacionFilter(), BasicAuthenticationFilter.class)
			.addFilterAfter(publicClientAutenticacionFilter(), BearerTokenAuthenticationFilter.class)
			
			.authorizeRequests()
				.anyRequest().authenticated()
				.antMatchers(".well-known/openid-configuration").permitAll()
			
			.and()
			.httpBasic()
			
			.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.csrf().disable();
	}
	
	/**
     * Configura los proveedores de autenticación. Sobrescribe la configuración de AuthenticationManagerBuilder
     * para añadir proveedores de autenticación personalizados.
     * 
     * @param auth Constructor para configurar los proveedores de autenticación.
     * @throws Exception si ocurre un error durante la configuración de los proveedores de autenticación.
     */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Configurar el proveedor de autenticación para tokens Bearer
		auth.authenticationProvider(bearerAuthenticationProvider);
		// Configurar el proveedor de autenticación para clientes públicos
		auth.authenticationProvider(publicClientAuthenticationProvider);
		// Configurar el proveedor de autenticación para autenticación básica
		auth.authenticationProvider(basicAuthenticationProvider());
	}

	/**
     * Define el bean AuthenticationManager que es el encargado de la autenticación en Spring Security.
     * 
     * @return {@link AuthenticationManager}
     * @throws Exception si ocurre un error durante la configuración del bean.
     */
//	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
     * Define el filtro de autenticación para tokens Bearer.
     * 
     * @return {@link BearerTokenAuthenticationFilter}
     * @throws Exception si ocurre un error durante la configuración del filtro.
     */
	@Bean
	public BearerTokenAuthenticationFilter bearerTokenAutenticacionFilter() throws Exception {
		return new BearerTokenAuthenticationFilter(authenticationManagerBean(), "/oauth/revoke", "/oauth/userinfo", "/oauth/introspect");
	}

	/**
     * Define el filtro de autenticación para clientes públicos.
     * 
     * @return {@link PublicClientAuthenticationFilter}
     * @throws Exception si ocurre un error durante la configuración del filtro.
     */
	@Bean
	public PublicClientAuthenticationFilter publicClientAutenticacionFilter() throws Exception {
		return new PublicClientAuthenticationFilter(authenticationManagerBean(), "/oauth/revoke");
	}

//	@Bean
//	public BearerAuthenticationProvider bearerAuthenticationProvider() throws Exception {
//		return new BearerAuthenticationProvider();
//	}
	
//	@Bean
//	public PublicClientAuthenticationProvider publicClientAuthenticationProvider() throws Exception {
//		return new PublicClientAuthenticationProvider();
//	}

	/**
     * Define el servicio de detalles de usuario. Permite cargar los datos del usuario desde una fuente.
     * 
     * @return {@link OauthUserDetailsService}
     */
	@Bean
	public OauthUserDetailsService usernameUserDetailsService() {
		return new OauthUserDetailsService();
	}

	@Bean
	public ClientDetailsUserDetailsService clientDetailsUserDetailsService() {
		return new ClientDetailsUserDetailsService(clientDetailsService);
	}

	@Bean
	public BasicAuthenticationProvider basicAuthenticationProvider() {
		BasicAuthenticationProvider basicAuthenticationProvider = new BasicAuthenticationProvider();
		basicAuthenticationProvider.setUserDetailsService(clientDetailsUserDetailsService());
		return basicAuthenticationProvider;
	}

	private static CharacterEncodingFilter getCharacterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(Boolean.TRUE.booleanValue());
		return filter;
	}
	
	@Bean
	public CorsFilter corsFilter() throws Exception {
		return new CorsFilter(corsConfigurationSource());
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
//		CorsConfiguration corsConfig = new CorsConfiguration();
//		corsConfig.setAllowedOrigins(origins);
//		corsConfig.setAllowedMethods(methods);
//		corsConfig.setAllowedHeaders(headers);
//		corsConfig.setAllowCredentials(allowCredentials);
//		corsConfig.setMaxAge(maxAge);
		
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedOrigins(Arrays.asList("capacitor://localhost", "http://localhost:8100", "http://localhost", "http://localhost:9081"));
		corsConfig.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		corsConfig.setAllowedHeaders(Arrays.asList("Origin", "X-Requested-With", "Content-Type", "Accept", "Authorization", "XMLHttpRequest"));
		corsConfig.setAllowCredentials(true);
		corsConfig.setMaxAge((long)3600);
		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}
	
}