package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoEje;
import ec.edu.espe.plantillaEspe.dto.DtoMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceMeta {
    DtoMeta find(String codigo);
    Page<DtoMeta> findAll(Pageable pageable);
    List<DtoMeta> findAll();
    List<DtoMeta> findAllActivos();
    Page<DtoMeta> findAllActivos(Pageable pageable);
    DtoMeta save(DtoMeta dtoMeta, String accessToken);
    DtoMeta update(DtoMeta dtoMeta, String accessToken);
    void delete(String codigo, String accessToken);
}
