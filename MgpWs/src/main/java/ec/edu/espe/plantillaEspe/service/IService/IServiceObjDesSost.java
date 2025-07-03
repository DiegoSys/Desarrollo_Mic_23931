package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoObjDesSost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceObjDesSost {
    DtoObjDesSost find(String codigo);
    Page<DtoObjDesSost> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    List<DtoObjDesSost> findAllActivos();
    DtoObjDesSost save(DtoObjDesSost dtoObjDesSost, String accessToken);
    DtoObjDesSost update(DtoObjDesSost dtoObjDesSost, String accessToken);
    void delete(String codigo, String accessToken);
}
