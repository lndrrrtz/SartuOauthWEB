package net.edu.sartuoauth.core.facades.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import net.edu.sartuoauth.core.daos.ClientDetailsServiceDao;
import net.edu.sartuoauth.core.facades.ClientDetailsServiceFacade;

@Service
public class ClientDetailsServiceFacadeImpl implements ClientDetailsServiceFacade {

	@Autowired
	private ClientDetailsServiceDao clientDetailsServiceDao;

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		return clientDetailsServiceDao.loadClientByClientId(clientId);
	}
}
