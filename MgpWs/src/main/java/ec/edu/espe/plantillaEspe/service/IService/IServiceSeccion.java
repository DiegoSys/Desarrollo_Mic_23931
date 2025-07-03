package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoSeccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceSeccion {
    DtoSeccion find(String codigo);
    List<DtoSeccion> findAll();
    List<DtoSeccion> findAllActivos();
    Page<DtoSeccion> findAll(Pageable pageable);
    Page<DtoSeccion> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoSeccion save(DtoSeccion dtoSeccion, String accessToken);
    DtoSeccion update(DtoSeccion dtoSeccion, String accessToken);
    void delete(String codigo, String accessToken);
}