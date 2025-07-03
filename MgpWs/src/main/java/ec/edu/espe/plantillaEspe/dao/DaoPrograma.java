package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Programa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link Programa}.
 * Permite buscar programas por código y filtrar por estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoPrograma extends JpaRepository<Programa, Long> {
    /**
     * Busca un programa por su código.
     *
     * @param codigo Código del programa.
     * @return Un Optional con el programa encontrado, si existe.
     */
    Optional<Programa> findByCodigo(String codigo);

    /**
     * Obtiene una lista de programas filtrados por estado.
     *
     * @param estado Estado del programa.
     * @return Lista de programas con el estado especificado.
     */
    List<Programa> findByEstado(Estado estado);

    /**
     * Obtiene una página de programas filtrados por estado.
     *
     * @param estado   Estado del programa.
     * @param pageable Información de paginación.
     * @return Página de programas con el estado especificado.
     */
    Page<Programa> findByEstado(Estado estado, Pageable pageable);
}