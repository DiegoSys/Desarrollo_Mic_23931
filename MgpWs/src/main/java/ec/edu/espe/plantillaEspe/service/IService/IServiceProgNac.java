package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoEje;
import ec.edu.espe.plantillaEspe.dto.DtoProgNac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceProgNac {
    DtoProgNac find(String codigo);
    Page<DtoProgNac> findAll(Pageable pageable);
    List<DtoProgNac> findAll();
    List<DtoProgNac> findAllActivos();
    Page<DtoProgNac> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoProgNac save(DtoProgNac dtoProgNac, String accessToken);
    DtoProgNac update(DtoProgNac dtoProgNac, String accessToken);
    void delete(String codigo, String accessToken);
}
