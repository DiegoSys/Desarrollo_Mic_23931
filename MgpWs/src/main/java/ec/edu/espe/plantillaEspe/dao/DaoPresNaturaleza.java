package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.PlanNacional;
import ec.edu.espe.plantillaEspe.model.PresNaturaleza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de la entidad {@link PresNaturaleza}.
 * Permite buscar naturalezas presupuestarias por código y filtrar por estado,
 * con soporte para paginación.
 */
public interface DaoPresNaturaleza extends JpaRepository<PresNaturaleza, Long> {
    /**
     * Busca una naturaleza presupuestaria por su código.
     *
     * @param codigo Código de la naturaleza.
     * @return Un Optional con la naturaleza encontrada, si existe.
     */
    Optional<PresNaturaleza> findByCodigo(String codigo);

    /**
     * Obtiene una lista de naturalezas presupuestarias filtradas por estado.
     *
     * @param estado Estado de la naturaleza.
     * @return Lista de naturalezas con el estado especificado.
     */
    List<PresNaturaleza> findByEstado(Estado estado);

    /**
     * Obtiene una página de naturalezas presupuestarias filtradas por estado.
     *
     * @param estado   Estado de la naturaleza.
     * @param pageable Información de paginación.
     * @return Página de naturalezas con el estado especificado.
     */
    Page<PresNaturaleza> findByEstado(Estado estado, Pageable pageable);
}