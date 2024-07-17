package net.edu.sartuoauth.core.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.edu.sartuoauth.core.beans.Restriccion;
import net.edu.sartuoauth.core.daos.RestriccionDao;
import net.edu.sartuoauth.core.enums.TipoRestriccion;
import net.edu.sartuoauth.core.facades.RestriccionFacade;

@Service
public class RestriccionFacadeImpl implements RestriccionFacade {

	@Autowired
	private RestriccionDao restriccionDao;

	
	
	@Override
	public List<Restriccion> leerRestricciones() {
		return restriccionDao.leerRestricciones();
	}

	@Override
	public List<Restriccion> leerRestricciones(TipoRestriccion tipoRestriccion) {
		return restriccionDao.leerRestricciones(tipoRestriccion);
	}
}
