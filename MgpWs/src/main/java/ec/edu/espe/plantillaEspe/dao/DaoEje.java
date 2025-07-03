package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.PlanNacional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad {@link Eje}.
 * Proporciona métodos para gestionar los ejes del plan nacional, permitiendo
 * búsquedas por código y estado, así como paginación de resultados.
 *
 * @author ITS
 */
public interface DaoEje extends JpaRepository<Eje, Long> {
    /**
     * Busca un eje por su código.
     *
     * @param codigo Código del eje.
     * @return Un Optional con el eje encontrado, si existe.
     */
    Optional<Eje> findByCodigo(String codigo);

    /**
     * Obtiene una lista de ejes filtrados por estado.
     *
     * @param estado Estado del eje.
     * @return Lista de ejes con el estado especificado.
     */
    List<Eje> findByEstado(Estado estado);

    /**
     * Obtiene una página de ejes filtrados por estado.
     *
     * @param estado   Estado del eje.
     * @param pageable Información de paginación.
     * @return Página de ejes con el estado especificado.
     */
    Page<Eje> findByEstado(Estado estado, Pageable pageable);
}