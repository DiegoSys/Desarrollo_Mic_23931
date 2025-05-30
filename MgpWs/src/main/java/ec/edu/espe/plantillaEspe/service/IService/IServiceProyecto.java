package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceProyecto {
    DtoProyecto find(String codigo);
    Page<DtoProyecto> findAll(Pageable pageable);
    List<DtoProyecto> findAll();
    DtoProyecto save(DtoProyecto dtoProyecto, String accessToken);
    DtoProyecto update(DtoProyecto dtoProyecto, String accessToken);
    void delete(String codigo);
}