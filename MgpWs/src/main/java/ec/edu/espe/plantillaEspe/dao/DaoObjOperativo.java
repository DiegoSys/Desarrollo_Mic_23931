package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ObjOperativo;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktobjoper.
 * Proporciona métodos para gestionar los objetivos operativos.
 *
 * @author ITS
 */
public interface DaoObjOperativo extends CrudRepository<ObjOperativo, String> {

}