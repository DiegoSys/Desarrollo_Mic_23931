package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ProgNac;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktprognac.
 * Proporciona métodos para gestionar los programas nacionales.
 *
 * @author ITS
 */
public interface DaoProgNac extends CrudRepository<ProgNac, String> {

}