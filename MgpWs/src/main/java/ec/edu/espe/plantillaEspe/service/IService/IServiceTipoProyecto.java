package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoTipoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceTipoProyecto {
    DtoTipoProyecto find(String codigo);
    List<DtoTipoProyecto> findAll();
    List<DtoTipoProyecto> findAllActivos();
    Page<DtoTipoProyecto> findAll(Pageable pageable);
    Page<DtoTipoProyecto> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoTipoProyecto save(DtoTipoProyecto dtoTipoProyecto, String accessToken);
    DtoTipoProyecto update(DtoTipoProyecto dtoTipoProyecto, String accessToken);
    void delete(String codigo, String accessToken);
}