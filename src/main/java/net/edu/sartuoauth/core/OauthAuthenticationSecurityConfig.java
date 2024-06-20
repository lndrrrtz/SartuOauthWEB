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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;

import net.edu.sartuoauth.core.security.handlers.OauthLogoutHandler;
import net.edu.sartuoauth.core.security.handlers.OauthLogoutSuccessHandler;
import net.edu.sartuoauth.core.security.oauths2.providers.LoginAuthenticationProvider;
import net.edu.sartuoauth.core.security.oauths2.services.OauthUserDetailsService;

@Order(2)
@Configuration
@EnableWebSecurity
@PropertySource("classpath:net/sartuoauth/core/security/configuration.properties")
//@ComponentScan(basePackages = { "net.edu.sartuoauth.security.crypto" })
public class OauthAuthenticationSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String URL_LOGIN = "/oauth/login";

	private static final String URL_LOGOUT = "/oauth/logout**";

	private static final String URL_SESION_CERRADA = "/oauth/sesionCerrada";
	
	@Autowired
	private CorsFilter corsFilter;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/oauth/**");
		http.csrf().disable();
		http.addFilterBefore(corsFilter, ChannelProcessingFilter.class);
		http.addFilterBefore(getCharacterEncodingFilter(), CsrfFilter.class);
		http.addFilterAfter(exceptionTranslationFilter(), ExceptionTranslationFilter.class);

		http
			.authorizeRequests()
				.antMatchers("/oauth/login**", "/", "/.well-known/**", "/oauth/error").permitAll()
				.anyRequest().authenticated()
			
			.and()
			.formLogin()
				.usernameParameter("anagrama").passwordParameter("contrasena")
				.loginPage(URL_LOGIN)
				.loginProcessingUrl(URL_LOGIN)
			
			.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher(URL_LOGOUT))
				.addLogoutHandler(oauthLogoutHandler())
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
		return new LoginAuthenticationProvider();
	}
	
	private static CharacterEncodingFilter getCharacterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(Boolean.TRUE.booleanValue());
		return filter;
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
