package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DaoProyecto extends JpaRepository<Proyecto, String> {
    Optional<Proyecto> findByCodigo(String codigo);
}
