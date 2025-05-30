package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoPlanNacional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServicePlanNacional {
    DtoPlanNacional find(String codigo);
    List<DtoPlanNacional> findAll();
    List<DtoPlanNacional> findAllActivos();
    Page<DtoPlanNacional> findAll(Pageable pageable);
    Page<DtoPlanNacional> findAllActivos(Pageable pageable);
    DtoPlanNacional save(DtoPlanNacional dtoPlanNacional, String accessToken);
    DtoPlanNacional update(DtoPlanNacional dtoPlanNacional, String accessToken);
    void delete(String codigo, String accessToken);
}
