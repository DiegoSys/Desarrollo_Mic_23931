package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoEje;
import ec.edu.espe.plantillaEspe.dto.DtoEstrategia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceEstrategia {
    DtoEstrategia find(String codigo);
    Page<DtoEstrategia> findAll(Pageable pageable);
    List<DtoEstrategia> findAll();
    List<DtoEstrategia> findAllActivos();
    Page<DtoEstrategia> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoEstrategia save(DtoEstrategia dtoEstrategia, String accessToken);
    DtoEstrategia update(DtoEstrategia dtoEstrategia, String accessToken);
    void delete(String codigo, String accessToken);
}
