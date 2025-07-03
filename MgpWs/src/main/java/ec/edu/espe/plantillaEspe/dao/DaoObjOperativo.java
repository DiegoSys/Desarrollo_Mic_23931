package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ObjOperativo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link ObjOperativo}.
 * Permite buscar objetivos operativos por código y filtrar por estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoObjOperativo extends JpaRepository<ObjOperativo, Long> {
    /**
     * Busca un objetivo operativo por su código.
     *
     * @param codigo Código del objetivo operativo.
     * @return Un Optional con el objetivo encontrado, si existe.
     */
    Optional<ObjOperativo> findByCodigo(String codigo);

    /**
     * Obtiene una lista de objetivos operativos filtrados por estado.
     *
     * @param estado Estado del objetivo operativo.
     * @return Lista de objetivos operativos con el estado especificado.
     */
    List<ObjOperativo> findByEstado(Estado estado);

    /**
     * Obtiene una página de objetivos operativos filtrados por estado.
     *
     * @param estado   Estado del objetivo operativo.
     * @param pageable Información de paginación.
     * @return Página de objetivos operativos con el estado especificado.
     */
    Page<ObjOperativo> findByEstado(Estado estado, Pageable pageable);
}