package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoSeccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceSeccion {
    DtoSeccion find(String codigo);
    Page<DtoSeccion> findAll(Pageable pageable);  // Cambiado a Page
    DtoSeccion save(DtoSeccion dtoSeccion, String accessToken);
    List<DtoSeccion> findAll();
    DtoSeccion update(DtoSeccion dtoSeccion, String accessToken);
    void delete(String codigo);
}