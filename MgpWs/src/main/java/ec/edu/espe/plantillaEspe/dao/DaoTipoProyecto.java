package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.TipoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DaoTipoProyecto extends JpaRepository<TipoProyecto, String> {
    Optional<TipoProyecto> findByCodigo(String codigo);
}
