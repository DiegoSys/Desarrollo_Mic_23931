package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.PresGrupo;
import ec.edu.espe.plantillaEspe.model.PresNaturaleza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link PresGrupo}.
 * Permite buscar grupos presupuestarios por código, filtrar por estado y naturaleza,
 * con soporte para paginación.
 */
public interface DaoPresGrupo extends JpaRepository<PresGrupo, Long> {
    /**
     * Busca un grupo presupuestario por su código.
     *
     * @param codigo Código del grupo.
     * @return Un Optional con el grupo encontrado, si existe.
     */
    Optional<PresGrupo> findByCodigo(String codigo);

    /**
     * Obtiene una lista de grupos presupuestarios filtrados por estado.
     *
     * @param estado Estado del grupo.
     * @return Lista de grupos con el estado especificado.
     */
    List<PresGrupo> findByEstado(Estado estado);

    /**
     * Obtiene una página de grupos presupuestarios filtrados por estado.
     *
     * @param estado   Estado del grupo.
     * @param pageable Información de paginación.
     * @return Página de grupos con el estado especificado.
     */
    Page<PresGrupo> findByEstado(Estado estado, Pageable pageable);

    /**
     * Obtiene una página de grupos presupuestarios por código de naturaleza y estado.
     *
     * @param codigo   Código de la naturaleza.
     * @param estado   Estado del grupo.
     * @param pageable Información de paginación.
     * @return Página de grupos filtrados por naturaleza y estado.
     */
    Page<PresGrupo> findByPresNaturaleza_CodigoAndEstado(String codigo, Estado estado, Pageable pageable);

    /**
     * Obtiene una lista de grupos presupuestarios por código de naturaleza y estado.
     *
     * @param codigo Código de la naturaleza.
     * @param estado Estado del grupo.
     * @return Lista de grupos filtrados por naturaleza y estado.
     */
    List<PresGrupo> findByPresNaturaleza_CodigoAndEstado(String codigo, Estado estado);
}