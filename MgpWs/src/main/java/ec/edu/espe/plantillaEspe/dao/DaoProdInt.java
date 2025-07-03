package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ProdInt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link ProdInt}.
 * Permite buscar productos institucionales por código y filtrar por estado,
 * con soporte para paginación.
 *
 * @author ITS
 */
public interface DaoProdInt extends JpaRepository<ProdInt, Long> {
    /**
     * Busca un producto institucional por su código.
     *
     * @param codigo Código del producto.
     * @return Un Optional con el producto encontrado, si existe.
     */
    Optional<ProdInt> findByCodigo(String codigo);

    /**
     * Obtiene una lista de productos institucionales filtrados por estado.
     *
     * @param estado Estado del producto.
     * @return Lista de productos con el estado especificado.
     */
    List<ProdInt> findByEstado(Estado estado);

    /**
     * Obtiene una página de productos institucionales filtrados por estado.
     *
     * @param estado   Estado del producto.
     * @param pageable Información de paginación.
     * @return Página de productos con el estado especificado.
     */
    Page<ProdInt> findByEstado(Estado estado, Pageable pageable);
}