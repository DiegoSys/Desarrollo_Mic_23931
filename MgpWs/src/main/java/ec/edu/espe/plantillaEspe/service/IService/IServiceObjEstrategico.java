package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoEje;
import ec.edu.espe.plantillaEspe.dto.DtoObjEstrategico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceObjEstrategico {
    DtoObjEstrategico find(String codigo);
    Page<DtoObjEstrategico> findAll(Pageable pageable);
    List<DtoObjEstrategico> findAll();
    List<DtoObjEstrategico> findAllActivos();
    Page<DtoObjEstrategico> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoObjEstrategico save(DtoObjEstrategico dtoObjEstrategico, String accessToken);
    DtoObjEstrategico update(DtoObjEstrategico dtoObjEstrategico, String accessToken);
    void delete(String codigo, String accessToken);
}
