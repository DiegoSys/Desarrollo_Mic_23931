package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.PlanNacional;
import ec.edu.espe.plantillaEspe.model.PresNaturaleza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DaoPresNaturaleza extends JpaRepository<PresNaturaleza, Long> {
    Optional<PresNaturaleza> findByCodigo(String codigo);
    List<PresNaturaleza> findByEstado(Estado estado);
    Page<PresNaturaleza> findByEstado(Estado estado, Pageable pageable);
}
