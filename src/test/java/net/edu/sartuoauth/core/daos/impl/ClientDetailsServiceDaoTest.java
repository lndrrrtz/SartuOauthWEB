package net.edu.sartuoauth.core.daos.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.edu.sartuoauth.core.OauthCore;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OauthCore.class)
public class ClientDetailsServiceDaoTest {

	@Autowired
	private ClientDetailsService clientDetailsService;

	@Test
	public void loadClientByClientId() {
		
		ClientDetails clientId = clientDetailsService.loadClientByClientId("prueba");
		
		Assert.assertNotNull(clientId);
	}
}

