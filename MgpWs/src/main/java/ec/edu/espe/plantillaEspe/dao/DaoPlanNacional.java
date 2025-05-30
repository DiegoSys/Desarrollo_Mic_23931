package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.ObjDesSost;
import ec.edu.espe.plantillaEspe.model.PlanNacional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad PlanNacional.
 * Proporciona métodos para gestionar los planes nacionales.
 */
public interface DaoPlanNacional extends JpaRepository<PlanNacional, Long> {
    Optional<PlanNacional> findByCodigo(String codigo);
    List<PlanNacional> findByEstado(Estado estado);
    Page<PlanNacional> findByEstado(Estado estado, Pageable pageable);
}
