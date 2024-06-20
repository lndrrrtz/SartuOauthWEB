package net.edu.sartuoauth.core.security.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.edu.sartuoauth.core.beans.Usuario;
import net.edu.sartuoauth.core.facades.UsuarioFacade;
import net.edu.sartuoauth.core.security.oauths2.models.UserInfo;
import net.edu.sartuoauth.core.security.oauths2.models.UsuarioOauth;

@RestController
public class UserInfoController {

	@Autowired
	private UsuarioFacade usuarioFacade;

	@RequestMapping("/oauth/userinfo")
	@ResponseBody
	public UserInfo getUserInfo(UsuarioOauth usuarioOauth) {

		Usuario usuario = usuarioFacade.leerUsuario(usuarioOauth.getIdUsuario());
		
		return new UserInfo(usuario);
	}
	
}
