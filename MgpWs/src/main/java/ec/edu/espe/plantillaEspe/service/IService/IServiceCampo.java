package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IServiceCampo {
    DtoCampo find(String codigo);
    Page<DtoCampo> findAll(Pageable pageable);  // Cambiado a Page
    DtoCampo save(DtoCampo dtoCampo);
    DtoCampo update(DtoCampo dtoCampo);
    void delete(String codigo);
}