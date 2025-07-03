package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceCampo {
    DtoCampo find(String codigo);
    List<DtoCampo> findAll();
    List<DtoCampo> findAllActivos();
    Page<DtoCampo> findAll(Pageable pageable);
    Page<DtoCampo> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoCampo save(DtoCampo dtoCampo, String accessToken);
    DtoCampo update(DtoCampo dtoCampo, String accessToken);
    void delete(String codigo, String accessToken);
}