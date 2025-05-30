package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoPresNaturaleza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServicePresNaturaleza {
    DtoPresNaturaleza find(String codigo);
    List<DtoPresNaturaleza> findAll();
    List<DtoPresNaturaleza> findAllActivos();
    Page<DtoPresNaturaleza> findAll(Pageable pageable);
    Page<DtoPresNaturaleza> findAllActivos(Pageable pageable);
    DtoPresNaturaleza save(DtoPresNaturaleza dtoPresNaturaleza, String accessToken);
    DtoPresNaturaleza update(DtoPresNaturaleza dtoPresNaturaleza, String accessToken);
    void delete(String codigo, String accessToken);
}