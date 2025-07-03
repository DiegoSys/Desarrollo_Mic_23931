package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.TipoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link TipoProyecto}.
 * Permite buscar tipos de proyecto por código y filtrar por estado,
 * con soporte para paginación.
 */
public interface DaoTipoProyecto extends JpaRepository<TipoProyecto, Long> {
    /**
     * Busca un tipo de proyecto por su código.
     *
     * @param codigo Código del tipo de proyecto.
     * @return Un Optional con el tipo de proyecto encontrado, si existe.
     */
    Optional<TipoProyecto> findByCodigo(String codigo);

    /**
     * Obtiene una lista de tipos de proyecto filtrados por estado.
     *
     * @param estado Estado del tipo de proyecto.
     * @return Lista de tipos de proyecto con el estado especificado.
     */
    List<TipoProyecto> findByEstado(Estado estado);

    /**
     * Obtiene una página de tipos de proyecto filtrados por estado.
     *
     * @param estado   Estado del tipo de proyecto.
     * @param pageable Información de paginación.
     * @return Página de tipos de proyecto con el estado especificado.
     */
    Page<TipoProyecto> findByEstado(Estado estado, Pageable pageable);
}