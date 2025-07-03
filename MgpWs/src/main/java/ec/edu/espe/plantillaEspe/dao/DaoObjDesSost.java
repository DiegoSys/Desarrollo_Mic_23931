package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ObjDesSost;
import ec.edu.espe.plantillaEspe.dto.Estado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link ObjDesSost}.
 * Permite buscar objetivos de desarrollo sostenible por código y filtrar por estado,
 * con soporte para paginación.
 * 
 * @author ITS
 */
public interface DaoObjDesSost extends JpaRepository<ObjDesSost, Long> {
    /**
     * Busca un objetivo de desarrollo sostenible por su código.
     *
     * @param codigo Código del objetivo.
     * @return Un Optional con el objetivo encontrado, si existe.
     */
    Optional<ObjDesSost> findByCodigo(String codigo);

    /**
     * Obtiene una lista de objetivos filtrados por estado.
     *
     * @param estado Estado del objetivo.
     * @return Lista de objetivos con el estado especificado.
     */
    List<ObjDesSost> findByEstado(Estado estado);

    /**
     * Obtiene una página de objetivos filtrados por estado.
     *
     * @param estado   Estado del objetivo.
     * @param pageable Información de paginación.
     * @return Página de objetivos con el estado especificado.
     */
    Page<ObjDesSost> findByEstado(Estado estado, Pageable pageable);
}