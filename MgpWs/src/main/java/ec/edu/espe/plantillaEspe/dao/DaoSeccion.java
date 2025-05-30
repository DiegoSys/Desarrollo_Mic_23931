package ec.edu.espe.plantillaEspe.dao;

import java.util.Optional;
import ec.edu.espe.plantillaEspe.model.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktsecciones.
 * Proporciona métodos para gestionar las secciones de proyectos.
 *
 * @author ITS
 */
public interface DaoSeccion extends JpaRepository<Seccion, String> {

    Optional<Seccion> findByCodigo(String codigo);

}
