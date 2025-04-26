package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.PlanNacional;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktplannacional.
 * Proporciona métodos para gestionar los planes nacionales.
 *
 * @author ITS
 */
public interface DaoPlanNacional extends CrudRepository<PlanNacional, String> {

}