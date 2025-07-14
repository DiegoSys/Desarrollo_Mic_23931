package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Seccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link Seccion}.
 * Permite buscar secciones por código y filtrar por estado,
 * con soporte para paginación.
 */
public interface DaoSeccion extends JpaRepository<Seccion, Long> {
    /**
     * Busca una sección por su código.
     *
     * @param codigo Código de la sección.
     * @return Un Optional con la sección encontrada, si existe.
     */
    Optional<Seccion> findByCodigo(String codigo);

    /**
     * Obtiene una lista de secciones filtradas por estado.
     *
     * @param estado Estado de la sección.
     * @return Lista de secciones con el estado especificado.
     */
    List<Seccion> findByEstado(Estado estado);

    /**
     * Obtiene una página de secciones filtradas por estado.
     *
     * @param estado   Estado de la sección.
     * @param pageable Información de paginación.
     * @return Página de secciones con el estado especificado.
     */
    Page<Seccion> findByEstado(Estado estado, Pageable pageable);

    /**
     * Busca una sección por su código.
     *
     * @return Un Optional con la sección encontrada, si existe.
     */
    @Query("SELECT s.codigo FROM Seccion s WHERE s.codigo LIKE 'SEC-%'")
    List<String> findAllCodigosLikeSec();

    /**
     * Verifica si existe una sección con el código especificado.
     *
     * @param codigo Código de la sección.
     * @return true si existe una sección con el código, false en caso contrario.
     */ 
    boolean existsByCodigo(String codigo);
}