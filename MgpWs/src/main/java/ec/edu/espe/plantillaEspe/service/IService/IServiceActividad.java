package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceActividad {
    DtoActividad find(String codigo);
    Page<DtoActividad> findAll(Pageable pageable);
    List<DtoActividad> findAll();
    DtoActividad save(DtoActividad dtoActividad, String accessToken);
    DtoActividad update(DtoActividad dtoActividad, String accessToken);
    void delete(String codigo);
}