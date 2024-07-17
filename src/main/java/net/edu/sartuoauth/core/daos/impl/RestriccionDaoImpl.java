package net.edu.sartuoauth.core.daos.impl;

import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import net.edu.sartuoauth.core.beans.Restriccion;
import net.edu.sartuoauth.core.daos.RestriccionDao;
import net.edu.sartuoauth.core.enums.TipoRestriccion;

@Repository
public class RestriccionDaoImpl implements RestriccionDao {

	private static final String TIPO = "tipo";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private RowMapper<Restriccion> rowMapper = new BeanPropertyRowMapper<>(Restriccion.class);
	
	private static final String LEERRESTRICCIONES_SQL = StringUtils.join("SELECT ID, TIPO, ACCION, DATOS, DESCRIPCION FROM " + Tablas.RESTRICCION);
	
	private static final String LEERRESTRICCIONESTIPO_SQL = StringUtils.join(LEERRESTRICCIONES_SQL + " WHERE TIPO = :", TIPO);
	
	@Override
	public List<Restriccion> leerRestricciones() {
		return jdbcTemplate.query(LEERRESTRICCIONES_SQL, rowMapper);
	}

	@Override
	public List<Restriccion> leerRestricciones(TipoRestriccion tipoRestriccion) {
		MapSqlParameterSource parametro = new MapSqlParameterSource();
		parametro.addValue(TIPO, tipoRestriccion, Types.VARCHAR);
		return namedParameterJdbcTemplate.query(LEERRESTRICCIONESTIPO_SQL, parametro, rowMapper);
	}

}