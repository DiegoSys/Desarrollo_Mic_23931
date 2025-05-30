package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ObjEstrategico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktobjestr.
 * Proporciona métodos para gestionar los objetivos estratégicos.
 *
 * @author ITS
 */
public interface DaoObjEstrategico extends JpaRepository<ObjEstrategico, Long> {
    Optional<ObjEstrategico> findByCodigo(String codigo);
    List<ObjEstrategico> findByEstado(Estado estado);
    Page<ObjEstrategico> findByEstado(Estado estado, Pageable pageable);
}