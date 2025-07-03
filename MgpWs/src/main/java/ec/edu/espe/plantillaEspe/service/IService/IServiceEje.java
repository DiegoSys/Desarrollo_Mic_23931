package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoEje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceEje {
    DtoEje find(String codigo);
    Page<DtoEje> findAll(Pageable pageable);
    List<DtoEje> findAll();
    List<DtoEje> findAllActivos();
    Page<DtoEje> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoEje save(DtoEje dtoEje, String accessToken);
    DtoEje update(DtoEje dtoEje, String accessToken);
    void delete(String codigo, String accessToken);
}