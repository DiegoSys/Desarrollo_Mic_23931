package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.SubPrograma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DaoSubPrograma extends JpaRepository<SubPrograma, Long> {
    Optional<SubPrograma> findByCodigo(String codigo);
}
