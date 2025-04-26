package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ObjDesSost;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktobjdessost.
 * Proporciona métodos para gestionar los objetivos de desarrollo sostenible.
 *
 * @author ITS
 */
public interface DaoObjDesSost extends CrudRepository<ObjDesSost, String> {

}