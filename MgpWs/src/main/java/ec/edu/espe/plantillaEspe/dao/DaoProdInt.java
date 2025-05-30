package ec.edu.espe.plantillaEspe.dao;

import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.model.ProdInt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad ModelUzktprodint.
 * Proporciona métodos para gestionar los productos institucionales.
 *
 * @author ITS
 */
public interface DaoProdInt extends JpaRepository<ProdInt, Long> {
    Optional<ProdInt> findByCodigo(String codigo);
    List<ProdInt> findByEstado(Estado estado);
    Page<ProdInt> findByEstado(Estado estado, Pageable pageable);
}