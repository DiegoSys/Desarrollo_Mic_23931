package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.POA;
import ec.edu.espe.plantillaEspe.dto.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link POA}.
 * Permite filtrar POA por estado, con soporte para paginación.
 */
public interface DaoPOA extends JpaRepository<POA, Long> {
    /**
     * Obtiene una lista de POA filtrados por estado.
     *
     * @param estado Estado del POA.
     * @return Lista de POA con el estado especificado.
     */
    List<POA> findByEstado(Estado estado);

    /**
     * Obtiene una página de POA filtrados por estado.
     *
     * @param estado   Estado del POA.
     * @param pageable Información de paginación.
     * @return Página de POA con el estado especificado.
     */
    Page<POA> findByEstado(Estado estado, Pageable pageable);
}