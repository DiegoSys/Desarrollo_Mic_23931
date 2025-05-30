package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoSubPrograma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceSubPrograma {
    DtoSubPrograma find(String codigo);
    Page<DtoSubPrograma> findAll(Pageable pageable);
    List<DtoSubPrograma> findAll();
    DtoSubPrograma save(DtoSubPrograma dtoSubPrograma, String accessToken);
    DtoSubPrograma update(DtoSubPrograma dtoSubPrograma, String accessToken);
    void delete(String codigo);
}