package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.PDN;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktpdn.
 * Proporciona métodos para gestionar las políticas del plan nacional.
 *
 * @author ITS
 */
public interface DaoPDN extends CrudRepository<PDN, String> {

}