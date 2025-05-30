package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceCampo {
    DtoCampo find(String codigo);
    Page<DtoCampo> findAll(Pageable pageable);  // Cambiado a Page
    List<DtoCampo> findAll();
    DtoCampo save(DtoCampo dtoCampo, String accessToken);
    DtoCampo update(DtoCampo dtoCampo, String accessToken);
    void delete(String codigo);

}