package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ProgInst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link ProgInst}.
 * Permite buscar programas institucionales por código y filtrar por estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoProgInst extends JpaRepository<ProgInst, Long> {
    /**
     * Busca un programa institucional por su código.
     *
     * @param codigo Código del programa.
     * @return Un Optional con el programa encontrado, si existe.
     */
    Optional<ProgInst> findByCodigo(String codigo);

    /**
     * Obtiene una lista de programas institucionales filtrados por estado.
     *
     * @param estado Estado del programa.
     * @return Lista de programas con el estado especificado.
     */
    List<ProgInst> findByEstado(Estado estado);

    /**
     * Obtiene una página de programas institucionales filtrados por estado.
     *
     * @param estado   Estado del programa.
     * @param pageable Información de paginación.
     * @return Página de programas con el estado especificado.
     */
    Page<ProgInst> findByEstado(Estado estado, Pageable pageable);
}