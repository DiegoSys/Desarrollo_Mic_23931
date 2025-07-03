package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoProyectoSeccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceProyectoSeccion {
    DtoProyectoSeccion find(String codigo);
    Page<DtoProyectoSeccion> findAll(Pageable pageable);
    List<DtoProyectoSeccion> findAll();
    Page<DtoProyectoSeccion> findByCodigoProyecto(String codigoProyecto, Pageable pageable);
    DtoProyectoSeccion save(DtoProyectoSeccion proyecto, String accessToken);
    DtoProyectoSeccion update(DtoProyectoSeccion proyecto, String accessToken);
    void delete(String codigo);
}