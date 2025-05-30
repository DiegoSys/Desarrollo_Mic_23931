package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ObjOperativo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktobjoper.
 * Proporciona métodos para gestionar los objetivos operativos.
 *
 * @author ITS
 */
public interface DaoObjOperativo extends JpaRepository<ObjOperativo, Long> {
    Optional<ObjOperativo> findByCodigo(String codigo);
    List<ObjOperativo> findByEstado(Estado estado);
    Page<ObjOperativo> findByEstado(Estado estado, Pageable pageable);
}