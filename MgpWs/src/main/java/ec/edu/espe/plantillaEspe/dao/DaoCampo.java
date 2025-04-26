package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Campo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktCampos.
 * Esta interfaz extiende CrudRepository para proporcionar operaciones básicas de CRUD.
 *
 * @author ITS
 */
public interface DaoCampo extends JpaRepository<Campo, String> {

}