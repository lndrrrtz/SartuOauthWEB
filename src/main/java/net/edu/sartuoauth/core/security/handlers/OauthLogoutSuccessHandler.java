package net.edu.sartuoauth.core.security.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class OauthLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		String url = super.determineTargetUrl(request, response);
		
		String redirectUri = request.getParameter("post_logout_redirect_uri");
		
		if (StringUtils.isBlank(redirectUri)) {
			redirectUri = request.getParameter("redirect_uri");
		
			if (StringUtils.isBlank(redirectUri)) {
				redirectUri = request.getHeader("redirect_uri");
				
				if (StringUtils.isBlank(redirectUri)) {
					redirectUri = request.getParameter("referer");
				}
			}
		}
		
		if (StringUtils.isNotBlank(redirectUri)) {
			url = redirectUri;
		}
		return url;
	}
	
}
