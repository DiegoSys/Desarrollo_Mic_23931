package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoOPN;
import ec.edu.espe.plantillaEspe.dto.DtoPDN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServicePDN {
    DtoPDN find(String codigo);
    Page<DtoPDN> findAll(Pageable pageable);
    List<DtoPDN> findAll();
    List<DtoPDN> findAllActivos();
    Page<DtoPDN> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoPDN save(DtoPDN dtoPDN, String accessToken);
    DtoPDN update(DtoPDN dtoPDN, String accessToken);
    void delete(String codigo, String accessToken);
}
