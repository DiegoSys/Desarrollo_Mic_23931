package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoPresItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServicePresItem {
    DtoPresItem find(String codigo);
    List<DtoPresItem> findAll();
    List<DtoPresItem> findAllActivos();
    Page<DtoPresItem> findAll(Pageable pageable);
    Page<DtoPresItem> findAllActivos(Pageable pageable);
    DtoPresItem save(DtoPresItem dtoPresItem, String accessToken);
    DtoPresItem update(DtoPresItem dtoPresItem, String accessToken);
    void delete(String codigo, String accessToken);
}