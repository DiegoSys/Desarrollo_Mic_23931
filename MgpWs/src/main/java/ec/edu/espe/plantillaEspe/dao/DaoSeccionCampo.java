package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.SeccionCampo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktSeccionesCampos.
 * Proporciona métodos para gestionar la relación entre secciones y campos.
 *
 * @author ITS
 */
public interface DaoSeccionCampo extends JpaRepository<SeccionCampo, String> {
    Optional<SeccionCampo> findByCodigo(String codigo);

}