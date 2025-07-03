package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Actividad;
import ec.edu.espe.plantillaEspe.model.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * DaoActividad es el repositorio JPA para la entidad {@link Actividad}.
 * Proporciona métodos para consultar actividades por diferentes criterios,
 * como código, proyecto, estado y combinaciones de estos.
 */
public interface DaoActividad extends JpaRepository<Actividad, Long> {

    /**
     * Busca una actividad por su código y el ID del proyecto asociado.
     *
     * @param codigo     Código de la actividad.
     * @param proyectoId ID del proyecto.
     * @return Un Optional con la actividad encontrada, si existe.
     */
    Optional<Actividad> findByCodigoAndProyecto_Id(String codigo, Long proyectoId);

    /**
     * Obtiene todas las actividades asociadas a un proyecto.
     *
     * @param proyectoId ID del proyecto.
     * @return Lista de actividades del proyecto.
     */
    List<Actividad> findByProyecto_Id(Long proyectoId);

    /**
     * Obtiene una página de actividades asociadas a un proyecto.
     *
     * @param proyectoId ID del proyecto.
     * @param pageable   Información de paginación.
     * @return Página de actividades del proyecto.
     */
    Page<Actividad> findByProyecto_Id(Long proyectoId, Pageable pageable);

    /**
     * Obtiene todas las actividades de un proyecto con un estado específico.
     *
     * @param proyectoId ID del proyecto.
     * @param estado     Estado de la actividad.
     * @return Lista de actividades filtradas por proyecto y estado.
     */
    List<Actividad> findByProyecto_IdAndEstado(Long proyectoId, Estado estado);

    /**
     * Obtiene una página de actividades de un proyecto con un estado específico.
     *
     * @param proyectoId ID del proyecto.
     * @param estado     Estado de la actividad.
     * @param pageable   Información de paginación.
     * @return Página de actividades filtradas por proyecto y estado.
     */
    Page<Actividad> findByProyecto_IdAndEstado(Long proyectoId, Estado estado, Pageable pageable);

    /**
     * Busca una actividad por su código.
     *
     * @param codigo Código de la actividad.
     * @return Un Optional con la actividad encontrada, si existe.
     */
    Optional<Actividad> findByCodigo(String codigo);

    /**
     * Obtiene todas las actividades con un estado específico.
     *
     * @param estado Estado de la actividad.
     * @return Lista de actividades filtradas por estado.
     */
    List<Actividad> findByEstado(Estado estado);

    /**
     * Obtiene una página de actividades con un estado específico.
     *
     * @param estado   Estado de la actividad.
     * @param pageable Información de paginación.
     * @return Página de actividades filtradas por estado.
     */
    Page<Actividad> findByEstado(Estado estado, Pageable pageable);

    /**
     * Obtiene todas las actividades de un proyecto y estado específicos.
     *
     * @param proyecto Proyecto asociado.
     * @param estado   Estado de la actividad.
     * @return Lista de actividades filtradas por proyecto y estado.
     */
    List<Actividad> findByProyectoAndEstado(Proyecto proyecto, Estado estado);

    /**
     * Obtiene una página de actividades de un proyecto y estado específicos.
     *
     * @param proyecto Proyecto asociado.
     * @param estado   Estado de la actividad.
     * @param pageable Información de paginación.
     * @return Página de actividades filtradas por proyecto y estado.
     */
    Page<Actividad> findByProyectoAndEstado(Proyecto proyecto, Estado estado, Pageable pageable);
}