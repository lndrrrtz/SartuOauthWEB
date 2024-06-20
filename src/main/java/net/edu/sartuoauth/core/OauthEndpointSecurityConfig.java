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

import net.edu.sartuoauth.core.daos.ClientDetailsServiceDao;
import net.edu.sartuoauth.core.security.filters.BearerTokenAuthenticationFilter;
import net.edu.sartuoauth.core.security.filters.PublicClientAuthenticationFilter;
import net.edu.sartuoauth.core.security.oauths2.providers.BasicAuthenticationProvider;
import net.edu.sartuoauth.core.security.oauths2.providers.BearerAuthenticationProvider;
import net.edu.sartuoauth.core.security.oauths2.providers.PublicClientAuthenticationProvider;
import net.edu.sartuoauth.core.security.oauths2.services.OauthUserDetailsService;

@Order(1)
@Configuration
@EnableWebSecurity
public class OauthEndpointSecurityConfig extends WebSecurityConfigurerAdapter {

//		@Autowired
//		private BasicAuthenticationProvider basicAuthenticationProvider;

	@Autowired
	private BearerAuthenticationProvider bearerAuthenticationProvider;

	@Autowired
	private ClientDetailsServiceDao clientDetailsServiceDao;

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
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(bearerAuthenticationProvider);
		auth.authenticationProvider(publicClientAuthenticationProvider);
		auth.authenticationProvider(basicAuthenticationProvider());
	}

//	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public BearerTokenAuthenticationFilter bearerTokenAutenticacionFilter() throws Exception {
		return new BearerTokenAuthenticationFilter(authenticationManagerBean(), "/oauth/revoke", "/oauth/userinfo", "/oauth/introspect");
	}

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

	@Bean
	public OauthUserDetailsService usernameUserDetailsService() {
		return new OauthUserDetailsService();
	}

	@Bean
	public ClientDetailsUserDetailsService clientDetailsUserDetailsService() {
		return new ClientDetailsUserDetailsService(clientDetailsServiceDao);
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