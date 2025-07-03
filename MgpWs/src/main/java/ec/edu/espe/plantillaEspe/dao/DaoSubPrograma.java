package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Programa;
import ec.edu.espe.plantillaEspe.model.SubPrograma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link SubPrograma}.
 * Permite buscar subprogramas por código, programa y estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoSubPrograma extends JpaRepository<SubPrograma, Long> {
    /**
     * Busca un subprograma por su código y el ID del programa asociado.
     *
     * @param codigo     Código del subprograma.
     * @param programaId ID del programa.
     * @return Un Optional con el subprograma encontrado, si existe.
     */
    Optional<SubPrograma> findByCodigoAndPrograma_Id(String codigo, Long programaId);

    /**
     * Busca un subprograma por su código.
     *
     * @param codigo Código del subprograma.
     * @return Un Optional con el subprograma encontrado, si existe.
     */
    Optional<SubPrograma> findByCodigo(String codigo);

    /**
     * Obtiene una lista de subprogramas filtrados por estado.
     *
     * @param estado Estado del subprograma.
     * @return Lista de subprogramas con el estado especificado.
     */
    List<SubPrograma> findByEstado(Estado estado);

    /**
     * Obtiene una página de subprogramas filtrados por estado.
     *
     * @param estado   Estado del subprograma.
     * @param pageable Información de paginación.
     * @return Página de subprogramas con el estado especificado.
     */
    Page<SubPrograma> findByEstado(Estado estado, Pageable pageable);

    /**
     * Obtiene una página de subprogramas filtrados por programa y estado.
     *
     * @param programa Programa asociado.
     * @param estado   Estado del subprograma.
     * @param pageable Información de paginación.
     * @return Página de subprogramas filtrados por programa y estado.
     */
    Page<SubPrograma> findByProgramaAndEstado(Programa programa, Estado estado, Pageable pageable);
}