package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ProgInst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktproginst.
 * Proporciona métodos para gestionar los programas institucionales.
 *
 * @author ITS
 */
public interface DaoProgInst extends JpaRepository<ProgInst, Long> {
    Optional<ProgInst> findByCodigo(String codigo);
    List<ProgInst> findByEstado(Estado estado);
    Page<ProgInst> findByEstado(Estado estado, Pageable pageable);
}
