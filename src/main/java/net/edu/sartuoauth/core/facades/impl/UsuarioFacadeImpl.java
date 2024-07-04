package net.edu.sartuoauth.core.facades.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.edu.sartuoauth.core.beans.Usuario;
import net.edu.sartuoauth.core.daos.UsuarioDao;
import net.edu.sartuoauth.core.facades.UsuarioFacade;

@Service
public class UsuarioFacadeImpl implements UsuarioFacade {

	@Autowired
	private UsuarioDao usuarioDao;
	
	@Override
	public Usuario leerUsuario(String id) {
		return usuarioDao.leerUsuario(id);
	}
	
}
