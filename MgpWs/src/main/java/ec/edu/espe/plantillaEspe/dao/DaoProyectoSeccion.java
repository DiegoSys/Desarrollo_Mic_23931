package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ProyectoSeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktTipoProyecSeccion.
 * Proporciona métodos para gestionar la relación entre tipos de proyectos y secciones.
 *
 * @author ITS
 */
public interface DaoProyectoSeccion extends JpaRepository<ProyectoSeccion, String> {
    Optional<ProyectoSeccion> findByCodigo(String codigo);
}
