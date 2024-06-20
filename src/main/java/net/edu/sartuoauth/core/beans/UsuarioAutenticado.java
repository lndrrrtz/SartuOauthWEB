package net.edu.sartuoauth.core.beans;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAutenticado implements UserDetails {

	private static final long serialVersionUID = -8646648960567171781L;

	private String username;
	
	private Collection<? extends GrantedAuthority> authorities;
	
	private boolean nifDuplicado;
	
	public UsuarioAutenticado() {
		super();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isNifDuplicado() {
		return nifDuplicado;
	}
	
	public void setNifDuplicado(boolean nifDuplicado) {
		this.nifDuplicado = nifDuplicado;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
