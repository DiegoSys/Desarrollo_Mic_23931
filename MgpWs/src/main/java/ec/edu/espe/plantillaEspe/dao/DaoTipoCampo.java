package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.TipoCampo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad {@link TipoCampo}.
 * Esta interfaz extiende JpaRepository para proporcionar operaciones básicas de CRUD.
 *
 * Métodos disponibles:
 * - findByCodigo(String codigo): Busca un tipo de campo por su código.
 * - findByEstado(Estado estado): Busca tipos de campo por estado.
 * - findByEstado(Estado estado, Pageable pageable): Busca tipos de campo por estado con paginación.
 *
 * @author ITS
 */
public interface DaoTipoCampo extends JpaRepository<TipoCampo, Long> {
    /**
     * Busca un tipo de campo por su código.
     *
     * @param codigo Código del tipo de campo.
     * @return Un Optional con el tipo de campo encontrado, si existe.
     */
    Optional<TipoCampo> findByCodigo(String codigo);

    /**
     * Obtiene una lista de tipos de campo filtrados por estado.
     *
     * @param estado Estado del tipo de campo.
     * @return Lista de tipos de campo con el estado especificado.
     */
    List<TipoCampo> findByEstado(Estado estado);

    /**
     * Obtiene una página de tipos de campo filtrados por estado.
     *
     * @param estado   Estado del tipo de campo.
     * @param pageable Información de paginación.
     * @return Página de tipos de campo con el estado especificado.
     */
    Page<TipoCampo> findByEstado(Estado estado, Pageable pageable);
}