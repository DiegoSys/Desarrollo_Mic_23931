package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.PresGrupo;
import ec.edu.espe.plantillaEspe.model.PresItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link PresItem}.
 * Permite buscar items presupuestarios por código, filtrar por estado y subgrupo,
 * con soporte para paginación.
 */
@Repository
public interface DaoPresItem extends JpaRepository<PresItem, Long> {
    /**
     * Busca un item presupuestario por su código.
     *
     * @param codigo Código del item.
     * @return Un Optional con el item encontrado, si existe.
     */
    Optional<PresItem> findByCodigo(String codigo);

    /**
     * Obtiene una lista de items presupuestarios filtrados por estado.
     *
     * @param estado Estado del item.
     * @return Lista de items con el estado especificado.
     */
    List<PresItem> findByEstado(Estado estado);

    /**
     * Obtiene una página de items presupuestarios filtrados por estado.
     *
     * @param estado   Estado del item.
     * @param pageable Información de paginación.
     * @return Página de items con el estado especificado.
     */
    Page<PresItem> findByEstado(Estado estado, Pageable pageable);

    /**
     * Obtiene una página de items presupuestarios por código de subgrupo y estado.
     *
     * @param codigo   Código del subgrupo.
     * @param estado   Estado del item.
     * @param pageable Información de paginación.
     * @return Página de items filtrados por subgrupo y estado.
     */
    Page<PresItem> findByPresSubgrupo_CodigoAndEstado(String codigo, Estado estado, Pageable pageable);

    /**
     * Obtiene una lista de items presupuestarios por código de subgrupo y estado.
     *
     * @param codigo Código del subgrupo.
     * @param estado Estado del item.
     * @return Lista de items filtrados por subgrupo y estado.
     */
    List<PresItem> findByPresSubgrupo_CodigoAndEstado(String codigo, Estado estado);
}