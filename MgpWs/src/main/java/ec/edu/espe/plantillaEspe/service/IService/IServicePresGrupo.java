package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoPresGrupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServicePresGrupo {
    DtoPresGrupo find(String codigo);
    List<DtoPresGrupo> findAll();
    List<DtoPresGrupo> findAllActivos();
    Page<DtoPresGrupo> findAll(Pageable pageable);
    Page<DtoPresGrupo> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    // Obtener todos los grupos por el codigo de naturaleza paginados con estado activo
    Page<DtoPresGrupo> findByPresNaturaleza_Codigo(String codigo, Pageable pageable, Map<String, String> searchCriteria);
    DtoPresGrupo save(DtoPresGrupo dtoPresGrupo, String accessToken);
    DtoPresGrupo update(DtoPresGrupo dtoPresGrupo, String accessToken);
    void delete(String codigo, String accessToken);
}