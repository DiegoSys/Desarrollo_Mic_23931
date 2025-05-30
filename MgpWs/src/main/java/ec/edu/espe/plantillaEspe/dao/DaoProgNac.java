package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ProgNac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktprognac.
 * Proporciona métodos para gestionar los programas nacionales.
 *
 * @author ITS
 */
public interface DaoProgNac extends JpaRepository<ProgNac, Long> {
    Optional<ProgNac> findByCodigo(String codigo);
    List<ProgNac> findByEstado(Estado estado);
    Page<ProgNac> findByEstado(Estado estado, Pageable pageable);
}