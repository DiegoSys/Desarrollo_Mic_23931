package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ObjEstrategico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link ObjEstrategico}.
 * Permite buscar objetivos estratégicos por código y filtrar por estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoObjEstrategico extends JpaRepository<ObjEstrategico, Long> {
    /**
     * Busca un objetivo estratégico por su código.
     *
     * @param codigo Código del objetivo estratégico.
     * @return Un Optional con el objetivo encontrado, si existe.
     */
    Optional<ObjEstrategico> findByCodigo(String codigo);

    /**
     * Obtiene una lista de objetivos estratégicos filtrados por estado.
     *
     * @param estado Estado del objetivo estratégico.
     * @return Lista de objetivos estratégicos con el estado especificado.
     */
    List<ObjEstrategico> findByEstado(Estado estado);

    /**
     * Obtiene una página de objetivos estratégicos filtrados por estado.
     *
     * @param estado   Estado del objetivo estratégico.
     * @param pageable Información de paginación.
     * @return Página de objetivos estratégicos con el estado especificado.
     */
    Page<ObjEstrategico> findByEstado(Estado estado, Pageable pageable);
}