package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.Meta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad {@link Meta}.
 * Proporciona métodos para gestionar las metas.
 *
 * @author ITS
 */
public interface DaoMeta extends JpaRepository<Meta, Long> {
    /**
     * Busca una meta por su código.
     *
     * @param codigo Código de la meta.
     * @return Un Optional con la meta encontrada, si existe.
     */
    Optional<Meta> findByCodigo(String codigo);

    /**
     * Obtiene una lista de metas filtradas por estado.
     *
     * @param estado Estado de la meta.
     * @return Lista de metas con el estado especificado.
     */
    List<Meta> findByEstado(Estado estado);

    /**
     * Obtiene una página de metas filtradas por estado.
     *
     * @param estado   Estado de la meta.
     * @param pageable Información de paginación.
     * @return Página de metas con el estado especificado.
     */
    Page<Meta> findByEstado(Estado estado, Pageable pageable);
}