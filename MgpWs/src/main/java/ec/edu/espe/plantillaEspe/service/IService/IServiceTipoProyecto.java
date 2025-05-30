package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoTipoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceTipoProyecto {
    DtoTipoProyecto find(String codigo);
    Page<DtoTipoProyecto> findAll(Pageable pageable);  // Cambiado a Page
    DtoTipoProyecto save(DtoTipoProyecto dtoTipoProyecto, String accessToken);
    List<DtoTipoProyecto> findAll();
    DtoTipoProyecto update(DtoTipoProyecto dtoTipoProyecto, String accessToken);
    void delete(String codigo);
}