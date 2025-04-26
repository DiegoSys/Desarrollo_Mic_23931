package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ProgInst;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktproginst.
 * Proporciona métodos para gestionar los programas institucionales.
 *
 * @author ITS
 */
public interface DaoProgInst extends CrudRepository<ProgInst, String> {

}