package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoEje;
import ec.edu.espe.plantillaEspe.dto.DtoObjOperativo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceObjOperativo {
    DtoObjOperativo find(String codigo);
    Page<DtoObjOperativo> findAll(Pageable pageable);
    List<DtoObjOperativo> findAll();
    List<DtoObjOperativo> findAllActivos();
    Page<DtoObjOperativo> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoObjOperativo save(DtoObjOperativo dtoObjOperativo, String token);
    DtoObjOperativo update(DtoObjOperativo dtoObjOperativo, String token);
    void delete(String codigo, String token);
}
