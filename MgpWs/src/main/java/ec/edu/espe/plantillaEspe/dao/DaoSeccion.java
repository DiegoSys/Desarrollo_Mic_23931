package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Seccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktsecciones.
 * Proporciona métodos para gestionar las secciones de proyectos.
 *
 * @author ITS
 */
public interface DaoSeccion extends JpaRepository<Seccion, String> {

}
