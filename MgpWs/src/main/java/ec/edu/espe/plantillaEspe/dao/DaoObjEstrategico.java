package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ObjEstrategico;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktobjestr.
 * Proporciona métodos para gestionar los objetivos estratégicos.
 *
 * @author ITS
 */
public interface DaoObjEstrategico extends CrudRepository<ObjEstrategico, String> {

}