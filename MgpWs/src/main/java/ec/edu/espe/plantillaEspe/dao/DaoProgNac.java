package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ProgNac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link ProgNac}.
 * Permite buscar programas nacionales por código y filtrar por estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoProgNac extends JpaRepository<ProgNac, Long> {
    /**
     * Busca un programa nacional por su código.
     *
     * @param codigo Código del programa nacional.
     * @return Un Optional con el programa encontrado, si existe.
     */
    Optional<ProgNac> findByCodigo(String codigo);

    /**
     * Obtiene una lista de programas nacionales filtrados por estado.
     *
     * @param estado Estado del programa nacional.
     * @return Lista de programas con el estado especificado.
     */
    List<ProgNac> findByEstado(Estado estado);

    /**
     * Obtiene una página de programas nacionales filtrados por estado.
     *
     * @param estado   Estado del programa nacional.
     * @param pageable Información de paginación.
     * @return Página de programas con el estado especificado.
     */
    Page<ProgNac> findByEstado(Estado estado, Pageable pageable);
}