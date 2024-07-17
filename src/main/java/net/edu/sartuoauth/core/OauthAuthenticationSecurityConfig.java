package net.edu.sartuoauth.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;

import net.edu.sartuoauth.core.security.filters.CapturaParametrosFilter;
import net.edu.sartuoauth.core.security.filters.RestriccionesFilter;
import net.edu.sartuoauth.core.security.handlers.OauthLogoutHandler;
import net.edu.sartuoauth.core.security.handlers.OauthLogoutSuccessHandler;
import net.edu.sartuoauth.core.security.oauth2.providers.LoginAuthenticationProvider;
import net.edu.sartuoauth.core.security.oauth2.services.OauthUserDetailsService;

@Order(2)
@Configuration
@EnableWebSecurity
@PropertySource("classpath:net/edu/sartuoauth/core/config/sartu.properties")
public class OauthAuthenticationSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String URL_LOGIN = "/oauth/login";

	private static final String URL_LOGOUT = "/oauth/logout**";

	private static final String URL_SESION_CERRADA = "/oauth/sesionCerrada";

	@Autowired
	private CorsFilter corsFilter;
	
	/**
	 * Configuración para evitar que los filtros de spring security se activen en las URLs de recursos
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
	  web.ignoring().antMatchers("/recursos/**");
	}
	
	/**
	 * Configura la seguridad web, incluyendo el inicio de sesión y el cierre de sesión.
	 * 
	 * @param http el objeto {@link HttpSecurity} utilizado para construir la configuración de seguridad.
	 * @throws Exception si ocurre un error en la configuración de la seguridad web.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/oauth/**");
		http.csrf().disable();
		http.addFilterBefore(corsFilter, ChannelProcessingFilter.class);
		http.addFilterBefore(getCharacterEncodingFilter(), CsrfFilter.class);
		http.addFilterAfter(exceptionTranslationFilter(), ExceptionTranslationFilter.class);
		http.addFilterBefore(restriccionesFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(capturaParametrosFilter(), UsernamePasswordAuthenticationFilter.class);
		http
			.authorizeRequests()
				.antMatchers("/oauth/login**", "/", "/.well-known/**", "/oauth/error", "/accessDenied").permitAll()
				.anyRequest().authenticated()
			
			.and()
			// Configura el formulario de inicio de sesión o login
			.formLogin()
			 	// Establece los campos del formulario que se utilizarán para el login
				.usernameParameter("idUsuario").passwordParameter("contrasena")
				// URL de página de login
				.loginPage(URL_LOGIN)
				// URL donde se gestionan las credenciales del usuario
				.loginProcessingUrl(URL_LOGIN)
			
			.and()
			
			// Configurar el cierre de sesión o logout
			.logout()
				// URL para el cierre de sesión
				.logoutRequestMatcher(new AntPathRequestMatcher(URL_LOGOUT))
				// Manejador de logout personalizado
				.addLogoutHandler(oauthLogoutHandler())
				// Manejador que se utiliza cuando el logout se ha realizado con éxito
				.logoutSuccessHandler(oauthLogoutSuccessHandler())

			.and()
			.sessionManagement()
				.sessionAuthenticationErrorUrl(URL_SESION_CERRADA)
//					.invalidSessionUrl(URL_SESION_CERRADA)
			.and()
				.exceptionHandling()
					.authenticationEntryPoint(authenticationEntryPoint())
			.and()
			.headers().cacheControl();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(loginAuthenticationProvider());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public OauthUserDetailsService usernameUserDetailsService() {
		return new OauthUserDetailsService();
	}
	
	@Bean
	public OauthLogoutHandler oauthLogoutHandler() {
		return new OauthLogoutHandler();
	}
	
	@Bean
	public LogoutSuccessHandler oauthLogoutSuccessHandler() {
		OauthLogoutSuccessHandler oauthLogoutSuccessHandler = new OauthLogoutSuccessHandler();
//		oauthLogoutSuccessHandler.setRemoveOAuth2ClientContext(Boolean.TRUE.booleanValue());
		return oauthLogoutSuccessHandler;
	}
	
	@Bean
	@Qualifier("loginAuthenticationProvider")
	public LoginAuthenticationProvider loginAuthenticationProvider() {
		LoginAuthenticationProvider loginAuthenticationProvider = new LoginAuthenticationProvider();
		loginAuthenticationProvider.setUserDetailsService(usernameUserDetailsService());
		return loginAuthenticationProvider;
	}
	
	private static CharacterEncodingFilter getCharacterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(Boolean.TRUE.booleanValue());
		return filter;
	}
	
	@Bean
	public RestriccionesFilter restriccionesFilter() {
		return new RestriccionesFilter();
	}
	
	@Bean
	public CapturaParametrosFilter capturaParametrosFilter() {
		return new CapturaParametrosFilter();
	}
	
	@Bean
	public static AuthenticationEntryPoint authenticationEntryPoint() {
		return new LoginUrlAuthenticationEntryPoint(URL_LOGIN);
	}

	public static ExceptionTranslationFilter exceptionTranslationFilter() {
		ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter(authenticationEntryPoint());
		AccessDeniedHandlerImpl accessDeniedHandlerImpl = new AccessDeniedHandlerImpl();
		accessDeniedHandlerImpl.setErrorPage(URL_LOGIN);
		exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandlerImpl);
		exceptionTranslationFilter.afterPropertiesSet();
		return exceptionTranslationFilter;
	}

	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
