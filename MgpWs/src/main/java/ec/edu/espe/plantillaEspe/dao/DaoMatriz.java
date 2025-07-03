package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.Matriz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DaoMatriz extends JpaRepository<Matriz, Long> {
}
