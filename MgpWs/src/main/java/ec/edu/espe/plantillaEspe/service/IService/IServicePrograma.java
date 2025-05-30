package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoPrograma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServicePrograma {
    DtoPrograma find(String codigo);
    Page<DtoPrograma> findAll(Pageable pageable);
    List<DtoPrograma> findAll();
    DtoPrograma save(DtoPrograma dtoPrograma, String accessToken);
    DtoPrograma update(DtoPrograma dtoPrograma, String accessToken);
    void delete(String codigo);
}