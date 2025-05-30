package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.PresGrupo;
import ec.edu.espe.plantillaEspe.model.PresNaturaleza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DaoPresGrupo extends JpaRepository<PresGrupo, Long> {
    Optional<PresGrupo> findByCodigo(String codigo);
    List<PresGrupo> findByEstado(Estado estado);
    Page<PresGrupo> findByEstado(Estado estado, Pageable pageable);
}
