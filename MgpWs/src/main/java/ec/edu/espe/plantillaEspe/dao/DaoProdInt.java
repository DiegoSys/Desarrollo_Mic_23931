package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ProdInt;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktprodint.
 * Proporciona métodos para gestionar los productos institucionales.
 *
 * @author ITS
 */
public interface DaoProdInt extends CrudRepository<ProdInt, String> {

}