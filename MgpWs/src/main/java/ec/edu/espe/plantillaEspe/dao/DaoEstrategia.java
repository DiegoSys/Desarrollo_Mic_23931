package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.Estrategia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad {@link Estrategia}.
 * Proporciona métodos para gestionar las estrategias.
 *
 * @author ITS
 */
public interface DaoEstrategia extends JpaRepository<Estrategia, Long> {
    /**
     * Busca una estrategia por su código.
     *
     * @param codigo Código de la estrategia.
     * @return Un Optional con la estrategia encontrada, si existe.
     */
    Optional<Estrategia> findByCodigo(String codigo);

    /**
     * Obtiene una lista de estrategias filtradas por estado.
     *
     * @param estado Estado de la estrategia.
     * @return Lista de estrategias con el estado especificado.
     */
    List<Estrategia> findByEstado(Estado estado);

    /**
     * Obtiene una página de estrategias filtradas por estado.
     *
     * @param estado   Estado de la estrategia.
     * @param pageable Información de paginación.
     * @return Página de estrategias con el estado especificado.
     */
    Page<Estrategia> findByEstado(Estado estado, Pageable pageable);
}