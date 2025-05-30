package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.Estrategia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktestrategia.
 * Proporciona métodos para gestionar las estrategias.
 *
 * @author ITS
 */
public interface DaoEstrategia extends JpaRepository<Estrategia, Long> {
    Optional<Estrategia> findByCodigo(String codigo);
    List<Estrategia> findByEstado(Estado estado);
    Page<Estrategia> findByEstado(Estado estado, Pageable pageable);
}