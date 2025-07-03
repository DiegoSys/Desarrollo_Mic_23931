package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Proyecto;
import ec.edu.espe.plantillaEspe.model.SubPrograma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link Proyecto}.
 * Permite buscar proyectos por código, subprograma, programa y estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoProyecto extends JpaRepository<Proyecto, Long> {
    /**
     * Busca un proyecto por su código.
     *
     * @param codigo Código del proyecto.
     * @return Un Optional con el proyecto encontrado, si existe.
     */
    Optional<Proyecto> findByCodigo(String codigo);

    /**
     * Busca un proyecto por su código, subprograma y programa.
     *
     * @param codigo        Código del proyecto.
     * @param subProgramaId ID del subprograma.
     * @param programaId    ID del programa.
     * @return Un Optional con el proyecto encontrado, si existe.
     */
    Optional<Proyecto> findByCodigoAndSubprograma_IdAndPrograma_Id(
        String codigo, Long subProgramaId, Long programaId);

    /**
     * Obtiene una página de proyectos filtrados por subprograma y programa.
     *
     * @param subProgramaId ID del subprograma.
     * @param programaId    ID del programa.
     * @param pageable      Información de paginación.
     * @return Página de proyectos filtrados.
     */
    Page<Proyecto> findBySubprograma_IdAndPrograma_Id(
        Long subProgramaId, Long programaId, Pageable pageable);

    /**
     * Obtiene una lista de proyectos filtrados por estado.
     *
     * @param estado Estado del proyecto.
     * @return Lista de proyectos con el estado especificado.
     */
    List<Proyecto> findByEstado(Estado estado);

    /**
     * Obtiene una página de proyectos filtrados por estado.
     *
     * @param estado   Estado del proyecto.
     * @param pageable Información de paginación.
     * @return Página de proyectos con el estado especificado.
     */
    Page<Proyecto> findByEstado(Estado estado, Pageable pageable);

    /**
     * Obtiene una lista de proyectos filtrados por subprograma y estado.
     *
     * @param subPrograma Subprograma asociado.
     * @param estado      Estado del proyecto.
     * @return Lista de proyectos filtrados.
     */
    List<Proyecto> findBySubprogramaAndEstado(SubPrograma subPrograma, Estado estado);

    /**
     * Obtiene una página de proyectos filtrados por subprograma y estado.
     *
     * @param subPrograma Subprograma asociado.
     * @param estado      Estado del proyecto.
     * @param pageable    Información de paginación.
     * @return Página de proyectos filtrados.
     */
    Page<Proyecto> findBySubprogramaAndEstado(SubPrograma subPrograma, Estado estado, Pageable pageable);

    /**
     * Obtiene una página de proyectos filtrados por subprograma, programa y estado.
     *
     * @param subProgramaId ID del subprograma.
     * @param programaId    ID del programa.
     * @param estado        Estado del proyecto.
     * @param pageable      Información de paginación.
     * @return Página de proyectos filtrados.
     */
    Page<Proyecto> findBySubprograma_IdAndPrograma_IdAndEstado(
        Long subProgramaId, Long programaId, Estado estado, Pageable pageable);
}