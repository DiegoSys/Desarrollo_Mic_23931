package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Estrategia;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktestrategia.
 * Proporciona métodos para gestionar las estrategias.
 *
 * @author ITS
 */
public interface DaoEstrategia extends CrudRepository<Estrategia, String> {

}