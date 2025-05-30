package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.model.ObjDesSost;
import ec.edu.espe.plantillaEspe.dto.Estado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DaoObjDesSost extends JpaRepository<ObjDesSost, Long> {
    Optional<ObjDesSost> findByCodigo(String codigo);
    List<ObjDesSost> findByEstado(Estado estado);
    Page<ObjDesSost> findByEstado(Estado estado, Pageable pageable);
}