package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoMatrizPoaConsulta;
import ec.edu.espe.plantillaEspe.dto.DtoPOA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceMatrizPoaConsulta {
    DtoPOA find(Long id);
    Page<DtoMatrizPoaConsulta> findAll(Pageable pageable);
    List<DtoMatrizPoaConsulta> findAll();
    List<DtoMatrizPoaConsulta> findAllActivos();
    Page<DtoMatrizPoaConsulta> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoPOA save(DtoPOA dtoPOA, String accessToken);
    DtoPOA update(Long id, DtoPOA dtoPOA, String accessToken);
    void delete(Long id, String accessToken);
}