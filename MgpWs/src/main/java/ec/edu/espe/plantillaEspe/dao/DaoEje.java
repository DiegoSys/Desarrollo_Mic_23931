package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.PlanNacional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzkteje.
 * Proporciona métodos para gestionar los ejes del plan nacional.
 *
 * @author ITS
 */
public interface DaoEje extends JpaRepository<Eje, Long> {
    Optional<Eje> findByCodigo(String codigo);
    List<Eje> findByEstado(Estado estado);
    Page<Eje> findByEstado(Estado estado, Pageable pageable);
}
