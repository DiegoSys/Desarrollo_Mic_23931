package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IServiceProyecto {
    DtoProyecto find(String codigo);
    Page<DtoProyecto> findAll(Pageable pageable);  // Cambiado a Page
    DtoProyecto save(DtoProyecto dtoProyecto);
    DtoProyecto update(DtoProyecto dtoProyecto);
    void delete(String codigo);
}