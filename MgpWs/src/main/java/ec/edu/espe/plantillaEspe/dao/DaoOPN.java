package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.OPN;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktopn.
 * Proporciona métodos para gestionar los objetivos del plan nacional.
 *
 * @author ITS
 */
public interface DaoOPN extends CrudRepository<OPN, String> {

}