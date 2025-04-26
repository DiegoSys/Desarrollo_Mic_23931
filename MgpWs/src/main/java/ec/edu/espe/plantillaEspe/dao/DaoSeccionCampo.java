package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.SeccionCampo;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktSeccionesCampos.
 * Proporciona métodos para gestionar la relación entre secciones y campos.
 *
 * @author ITS
 */
public interface DaoSeccionCampo extends CrudRepository<SeccionCampo, String> {

}