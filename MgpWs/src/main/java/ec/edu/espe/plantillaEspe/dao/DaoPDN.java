package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.OPN;
import ec.edu.espe.plantillaEspe.model.PDN;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link PDN}.
 * Permite buscar políticas del plan nacional por código y filtrar por estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoPDN extends JpaRepository<PDN, Long> {
    /**
     * Busca una política del plan nacional por su código.
     *
     * @param codigo Código de la política.
     * @return Un Optional con la política encontrada, si existe.
     */
    Optional<PDN> findByCodigo(String codigo);

    /**
     * Obtiene una lista de políticas del plan nacional filtradas por estado.
     *
     * @param estado Estado de la política.
     * @return Lista de políticas con el estado especificado.
     */
    List<PDN> findByEstado(Estado estado);

    /**
     * Obtiene una página de políticas del plan nacional filtradas por estado.
     *
     * @param estado   Estado de la política.
     * @param pageable Información de paginación.
     * @return Página de políticas con el estado especificado.
     */
    Page<PDN> findByEstado(Estado estado, Pageable pageable);
}