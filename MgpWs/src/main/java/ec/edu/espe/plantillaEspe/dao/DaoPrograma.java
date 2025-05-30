package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DaoPrograma extends JpaRepository<Programa, Long> {
    Optional<Programa> findByCodigo(String codigo);
}
