package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoSeccionCampo;
import ec.edu.espe.plantillaEspe.model.SeccionCampo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceSeccionCampo {
    DtoSeccionCampo find(String codigo);
    Page<DtoSeccionCampo> findAll(Pageable pageable);
    List<DtoSeccionCampo> findAll();
    DtoSeccionCampo save(DtoSeccionCampo seccionCampo, String accessToken);
    DtoSeccionCampo update(DtoSeccionCampo seccionCampo, String accessToken);
    void delete(String codigo);
}