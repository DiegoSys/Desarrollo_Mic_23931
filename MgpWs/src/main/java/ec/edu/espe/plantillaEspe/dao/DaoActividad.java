package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DaoActividad extends JpaRepository<Actividad, Long> {
    Optional<Actividad> findByCodigo(String codigo);
}