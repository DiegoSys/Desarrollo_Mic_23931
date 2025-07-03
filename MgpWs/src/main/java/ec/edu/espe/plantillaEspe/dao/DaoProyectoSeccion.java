package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ProyectoSeccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link ProyectoSeccion}.
 * Permite buscar secciones de proyectos por código.
 *
 * @author ITS
 */
public interface DaoProyectoSeccion extends JpaRepository<ProyectoSeccion, String> {
    /**
     * Busca una sección de proyecto por su código.
     *
     * @param codigo Código de la sección.
     * @return Un Optional con la sección encontrada, si existe.
     */
    Optional<ProyectoSeccion> findByCodigo(String codigo);

    /**
     * Busca todas las secciones de un proyecto por el código del tipo de proyecto con paginación.
     *
     * @param codigo Código del tipo de proyecto.
     * @param pageable Parámetros de paginación.
     * @return Página con las secciones del proyecto encontradas.
     */
    Page<ProyectoSeccion> findByTipoProyectoCodigo(String codigo, Pageable pageable);

    /**
     * Busca todas las relaciones de una sección específica.
     *
     * @param codigoSeccion Código de la sección.
     * @return Lista de relaciones proyecto-sección.
     */
    List<ProyectoSeccion> findBySeccionCodigo(String codigoSeccion);

}