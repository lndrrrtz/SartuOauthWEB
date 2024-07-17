package net.edu.sartuoauth.core.daos.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import net.edu.sartuoauth.core.beans.RegistroAuditoria;
import net.edu.sartuoauth.core.daos.RegistroAuditoriaDao;

@Repository
public class RegistroAuditoriaDaoImpl implements RegistroAuditoriaDao {

	private static final String IP = "ip";
	private static final String FLUJO = "flujo";
	private static final String CLIENTID = "clientId";
	private static final String IDUSUARIO = "idUsuario";
	private static final String RESULTADO = "resultado";
	private static final String FECHA = "fecha";
	private static final String SCOPE = "scope";
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final String INSERTARREGISTROAUDITORIA_SQL = StringUtils.join("INSERT INTO ", Tablas.REGISTROAUDITORIA, " (IP, FLUJO, CLIENTID, IDUSUARIO, RESULTADO, FECHA, SCOPE) VALUES (:", IP, ", :", FLUJO, ", :", CLIENTID, ", :", IDUSUARIO, ", :", RESULTADO, ", :", FECHA, ", :", SCOPE, ")");
	
	@Override
	public int registrarAcceso(RegistroAuditoria registroAuditoria) {
		BeanPropertySqlParameterSource parametros = new BeanPropertySqlParameterSource(registroAuditoria);
		return namedParameterJdbcTemplate.update(INSERTARREGISTROAUDITORIA_SQL, parametros);
	}
}