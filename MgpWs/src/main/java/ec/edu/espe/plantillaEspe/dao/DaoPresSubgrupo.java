package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.PresSubgrupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DaoPresSubgrupo extends JpaRepository<PresSubgrupo, Long> {
    Optional<PresSubgrupo> findByCodigo(String codigo);
    List<PresSubgrupo> findByEstado(Estado estado);
    Page<PresSubgrupo> findByEstado(Estado estado, Pageable pageable);
}
