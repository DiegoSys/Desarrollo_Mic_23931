package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoPresItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServicePresItem {
    DtoPresItem find(String codigo);
    List<DtoPresItem> findAll();
    List<DtoPresItem> findAllActivos();
    Page<DtoPresItem> findAll(Pageable pageable);
    // Obtener todos los items paginados con estado activo y criterios de búsqueda
    Page<DtoPresItem> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    // Obtener todos los items por el codigo de subgrupo paginados con estado activo y criterios de búsqueda
    Page<DtoPresItem> findByPresSubgrupo_Codigo(String codigo, Pageable pageable, Map<String, String> searchCriteria);
    DtoPresItem save(DtoPresItem dtoPresItem, String accessToken);
    DtoPresItem update(DtoPresItem dtoPresItem, String accessToken);
    void delete(String codigo, String accessToken);
}