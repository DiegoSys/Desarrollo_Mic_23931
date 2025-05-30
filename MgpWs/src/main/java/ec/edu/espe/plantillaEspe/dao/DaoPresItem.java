package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.PresGrupo;
import ec.edu.espe.plantillaEspe.model.PresItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DaoPresItem extends JpaRepository<PresItem, Long> {
    Optional<PresItem> findByCodigo(String codigo);
    List<PresItem> findByEstado(Estado estado);
    Page<PresItem> findByEstado(Estado estado, Pageable pageable);
}