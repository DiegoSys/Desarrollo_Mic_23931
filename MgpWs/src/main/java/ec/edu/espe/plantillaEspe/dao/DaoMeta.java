package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.Meta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktmeta.
 * Proporciona métodos para gestionar las metas.
 *
 * @author ITS
 */
public interface DaoMeta extends JpaRepository<Meta, Long> {
    Optional<Meta> findByCodigo(String codigo);
    List<Meta> findByEstado(Estado estado);
    Page<Meta> findByEstado(Estado estado, Pageable pageable);
}