package net.edu.sartuoauth.core.facades.impl;

import org.springframework.beans.factory.annotation.Autowired;

import net.edu.sartuoauth.core.beans.Usuario;
import net.edu.sartuoauth.core.daos.UsuarioDao;
import net.edu.sartuoauth.core.facades.UsuarioFacade;

public class UsuarioFacadeImpl implements UsuarioFacade{

	@Autowired
	private UsuarioDao usuarioDao;
	
	@Override
	public Usuario leerUsuario(String codu) {
		return usuarioDao.leerUsuario(codu);
	}
	
}
