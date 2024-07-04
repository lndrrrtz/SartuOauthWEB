package net.edu.sartuoauth.core.daos.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import net.edu.sartuoauth.core.beans.Usuario;
import net.edu.sartuoauth.core.daos.UsuarioDao;

@Repository
public class UsuarioDaoImpl implements UsuarioDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private RowMapper<Usuario> rowMapper = new BeanPropertyRowMapper<>(Usuario.class);
	
	private static final String LEERUSUARIO_SQL = "SELECT ID, CONTRASENA, NOMBRE, DNI, EMAIL, TIPO FROM " + Tablas.USUARIO + " WHERE ID = :id";
	
	@Override
	public Usuario leerUsuario(String id) {
		Map<String, String> parametro = new HashMap<>();
		parametro.put("id", id);
		try {
			return namedParameterJdbcTemplate.queryForObject(LEERUSUARIO_SQL, parametro, rowMapper);
		} catch (Exception e) {
			return null;
		}
	}
}