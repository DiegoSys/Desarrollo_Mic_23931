package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.OPN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktopn.
 * Proporciona métodos para gestionar los objetivos del plan nacional.
 *
 * @author ITS
 */
public interface DaoOPN extends JpaRepository<OPN, Long> {
    Optional<OPN> findByCodigo(String codigo);
    List<OPN> findByEstado(Estado estado);
    Page<OPN> findByEstado(Estado estado, Pageable pageable);
}