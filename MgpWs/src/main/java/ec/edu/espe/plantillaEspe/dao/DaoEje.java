package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Eje;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzkteje.
 * Proporciona métodos para gestionar los ejes del plan nacional.
 *
 * @author ITS
 */
public interface DaoEje extends CrudRepository<Eje, String> {

}