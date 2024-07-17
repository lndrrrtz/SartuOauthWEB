package net.edu.sartuoauth.core.security.oauth2.services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import net.edu.sartuoauth.core.beans.Usuario;
import net.edu.sartuoauth.core.beans.UsuarioAutenticado;
import net.edu.sartuoauth.core.daos.UsuarioDao;

@Service
public class OauthUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UsuarioDao usuarioDao;

	@Override
	public UserDetails loadUserByUsername(String identificacionUsuario) {
		
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		Usuario usuario = usuarioDao.leerUsuario(identificacionUsuario);

		BeanUtils.copyProperties(usuario, usuarioAutenticado);
		usuarioAutenticado.setUsername(usuario.getId());

		// Roles
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		GrantedAuthority usuarioAuthority = new SimpleGrantedAuthority(usuario.getTipo().toString());
		authorities.add(usuarioAuthority);
		usuarioAutenticado.setAuthorities(authorities);
		
		return usuarioAutenticado;
	}
}
