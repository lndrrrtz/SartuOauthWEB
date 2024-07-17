package net.edu.sartuoauth.core.facades;

import java.util.List;

import net.edu.sartuoauth.core.beans.Restriccion;
import net.edu.sartuoauth.core.enums.TipoRestriccion;

public interface RestriccionFacade {
	
	/**
	 * Obtiene todas las restricciones
	 * 
	 * @return {@link List} de {@link Restriccion} con datos de las restricciones
	 */
	List<Restriccion> leerRestricciones();
	
	/**
	 * Obtiene todas las restricciones de un tipo
	 * 
	 * @return {@link List} de {@link Restriccion} con datos de las restricciones
	 */
	List<Restriccion> leerRestricciones(TipoRestriccion tipoRestriccion);
}
