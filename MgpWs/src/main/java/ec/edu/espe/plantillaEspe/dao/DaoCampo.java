package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Campo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad {@link Campo}.
 * Esta interfaz extiende JpaRepository para proporcionar operaciones básicas de CRUD.
 *
 * Métodos disponibles:
 * - findByCodigo(String codigo): Busca un campo por su código.
 * - findByEstado(Estado estado): Busca campos por estado.
 * - findByEstado(Estado estado, Pageable pageable): Busca campos por estado con paginación.
 *
 * @author ITS
 */
public interface DaoCampo extends JpaRepository<Campo, String> {
    /**
     * Busca un campo por su código.
     *
     * @param codigo Código del campo.
     * @return Un Optional con el campo encontrado, si existe.
     */
    Optional<Campo> findByCodigo(String codigo);

    /**
     * Obtiene una lista de campos filtrados por estado.
     *
     * @param estado Estado del campo.
     * @return Lista de campos con el estado especificado.
     */
    List<Campo> findByEstado(Estado estado);

    /**
     * Obtiene una página de campos filtrados por estado.
     *
     * @param estado   Estado del campo.
     * @param pageable Información de paginación.
     * @return Página de campos con el estado especificado.
     */
    Page<Campo> findByEstado(Estado estado, Pageable pageable);
}