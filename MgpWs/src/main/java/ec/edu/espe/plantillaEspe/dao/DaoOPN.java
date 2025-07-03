package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.OPN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link OPN}.
 * Permite buscar objetivos del plan nacional por código y filtrar por estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoOPN extends JpaRepository<OPN, Long> {
    /**
     * Busca un objetivo del plan nacional por su código.
     *
     * @param codigo Código del objetivo.
     * @return Un Optional con el objetivo encontrado, si existe.
     */
    Optional<OPN> findByCodigo(String codigo);

    /**
     * Obtiene una lista de objetivos del plan nacional filtrados por estado.
     *
     * @param estado Estado del objetivo.
     * @return Lista de objetivos con el estado especificado.
     */
    List<OPN> findByEstado(Estado estado);

    /**
     * Obtiene una página de objetivos del plan nacional filtrados por estado.
     *
     * @param estado   Estado del objetivo.
     * @param pageable Información de paginación.
     * @return Página de objetivos con el estado especificado.
     */
    Page<OPN> findByEstado(Estado estado, Pageable pageable);
}