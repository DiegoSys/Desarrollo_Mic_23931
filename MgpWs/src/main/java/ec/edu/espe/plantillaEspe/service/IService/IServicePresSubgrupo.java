package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoPresSubgrupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServicePresSubgrupo {
    DtoPresSubgrupo find(String codigo);
    List<DtoPresSubgrupo> findAll();
    List<DtoPresSubgrupo> findAllActivos();
    Page<DtoPresSubgrupo> findAll(Pageable pageable);
    Page<DtoPresSubgrupo> findAllActivos(Pageable pageable);
    // Obtener todos los subGrupos por el codigo de grupos paginados con estado activo
    //Page<DtoPresSubgrupo> findByPresGrupo_Codigo(String codigo, Pageable pageable);
    Page<DtoPresSubgrupo> findByPresGrupo_Codigo(String codigo, Pageable pageable, Map<String, String> searchCriteria);

    DtoPresSubgrupo save(DtoPresSubgrupo dtoPresSubgrupo, String accessToken);
    DtoPresSubgrupo update(DtoPresSubgrupo dtoPresSubgrupo, String accessToken);
    void delete(String codigo, String accessToken);
}