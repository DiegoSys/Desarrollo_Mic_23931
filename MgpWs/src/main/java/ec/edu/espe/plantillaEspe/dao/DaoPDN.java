package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.OPN;
import ec.edu.espe.plantillaEspe.model.PDN;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktpdn.
 * Proporciona métodos para gestionar las políticas del plan nacional.
 *
 * @author ITS
 */
public interface DaoPDN extends JpaRepository<PDN, Long> {
    Optional<PDN> findByCodigo(String codigo);
    List<PDN> findByEstado(Estado estado);
    Page<PDN> findByEstado(Estado estado, Pageable pageable);
}
