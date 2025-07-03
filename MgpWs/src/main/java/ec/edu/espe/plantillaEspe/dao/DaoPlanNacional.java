package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.ObjDesSost;
import ec.edu.espe.plantillaEspe.model.PlanNacional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link PlanNacional}.
 * Permite buscar planes nacionales por código y filtrar por estado,
 * con soporte para paginación.
 */
public interface DaoPlanNacional extends JpaRepository<PlanNacional, Long> {
    /**
     * Busca un plan nacional por su código.
     *
     * @param codigo Código del plan nacional.
     * @return Un Optional con el plan nacional encontrado, si existe.
     */
    Optional<PlanNacional> findByCodigo(String codigo);

    /**
     * Obtiene una lista de planes nacionales filtrados por estado.
     *
     * @param estado Estado del plan nacional.
     * @return Lista de planes nacionales con el estado especificado.
     */
    List<PlanNacional> findByEstado(Estado estado);

    /**
     * Obtiene una página de planes nacionales filtrados por estado.
     *
     * @param estado   Estado del plan nacional.
     * @param pageable Información de paginación.
     * @return Página de planes nacionales con el estado especificado.
     */
    Page<PlanNacional> findByEstado(Estado estado, Pageable pageable);
}