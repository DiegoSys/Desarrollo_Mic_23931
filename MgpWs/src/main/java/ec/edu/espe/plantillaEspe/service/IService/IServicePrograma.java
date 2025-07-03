package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoPrograma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServicePrograma {
    DtoPrograma find(Long id);
    DtoPrograma findByCodigo(String codigo);
    Page<DtoPrograma> findAll(Pageable pageable);
    List<DtoPrograma> findAll();
    List<DtoPrograma> findAllActivos();
    Page<DtoPrograma> findAllActivos(Pageable pageable);
    Page<DtoPrograma> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoPrograma save(DtoPrograma dtoPrograma, String accessToken);
    DtoPrograma update(DtoPrograma dtoPrograma, String accessToken);
    void delete(Long id, String accessToken);
}