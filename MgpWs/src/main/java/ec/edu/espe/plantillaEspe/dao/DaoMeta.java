package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Meta;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktmeta.
 * Proporciona métodos para gestionar las metas.
 *
 * @author ITS
 */
public interface DaoMeta extends CrudRepository<Meta, String> {

}