package net.edu.sartuoauth.core.facades.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.edu.sartuoauth.core.beans.RegistroAuditoria;
import net.edu.sartuoauth.core.daos.RegistroAuditoriaDao;
import net.edu.sartuoauth.core.facades.RegistroAuditoriaFacade;

@Service
public class RegistroAuditoriaFacadeImpl implements RegistroAuditoriaFacade {

	@Autowired
	private RegistroAuditoriaDao registroAuditoriaDao;
	
	@Override
	public int registrarAcceso(RegistroAuditoria registroAuditoria) {
		return registroAuditoriaDao.registrarAcceso(registroAuditoria);
	}
}
