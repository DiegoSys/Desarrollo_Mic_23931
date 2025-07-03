package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoEje;
import ec.edu.espe.plantillaEspe.dto.DtoProgInst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceProgInst {
    DtoProgInst find(String codigo);
    Page<DtoProgInst> findAll(Pageable pageable);
    List<DtoProgInst> findAll();
    List<DtoProgInst> findAllActivos();
    Page<DtoProgInst> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoProgInst save(DtoProgInst dtoProgInst, String accessToken);
    DtoProgInst update(DtoProgInst dtoProgInst, String accessToken);
    void delete(String codigo, String accessToken);
}
