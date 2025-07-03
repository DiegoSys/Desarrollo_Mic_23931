package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.PresSubgrupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link PresSubgrupo}.
 * Permite buscar subgrupos presupuestarios por código, filtrar por estado y grupo,
 * con soporte para paginación.
 */
@Repository
public interface DaoPresSubgrupo extends JpaRepository<PresSubgrupo, Long> {
    /**
     * Busca un subgrupo presupuestario por su código.
     *
     * @param codigo Código del subgrupo.
     * @return Un Optional con el subgrupo encontrado, si existe.
     */
    Optional<PresSubgrupo> findByCodigo(String codigo);

    /**
     * Obtiene una lista de subgrupos presupuestarios filtrados por estado.
     *
     * @param estado Estado del subgrupo.
     * @return Lista de subgrupos con el estado especificado.
     */
    List<PresSubgrupo> findByEstado(Estado estado);

    /**
     * Obtiene una página de subgrupos presupuestarios filtrados por estado.
     *
     * @param estado   Estado del subgrupo.
     * @param pageable Información de paginación.
     * @return Página de subgrupos con el estado especificado.
     */
    Page<PresSubgrupo> findByEstado(Estado estado, Pageable pageable);

    /**
     * Obtiene una página de subgrupos presupuestarios por código de grupo y estado.
     *
     * @param codigo   Código del grupo.
     * @param estado   Estado del subgrupo.
     * @param pageable Información de paginación.
     * @return Página de subgrupos filtrados por grupo y estado.
     */
    Page<PresSubgrupo> findByPresGrupo_CodigoAndEstado(String codigo, Estado estado, Pageable pageable);

    /**
     * Obtiene una lista de subgrupos presupuestarios por código de grupo y estado.
     *
     * @param codigo Código del grupo.
     * @param estado Estado del subgrupo.
     * @return Lista de subgrupos filtrados por grupo y estado.
     */
    List<PresSubgrupo> findByPresGrupo_CodigoAndEstado(String codigo, Estado estado);
}