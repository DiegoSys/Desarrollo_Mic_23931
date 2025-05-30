package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoPresSubgrupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServicePresSubgrupo {
    DtoPresSubgrupo find(String codigo);
    List<DtoPresSubgrupo> findAll();
    List<DtoPresSubgrupo> findAllActivos();
    Page<DtoPresSubgrupo> findAll(Pageable pageable);
    Page<DtoPresSubgrupo> findAllActivos(Pageable pageable);
    DtoPresSubgrupo save(DtoPresSubgrupo dtoPresSubgrupo, String accessToken);
    DtoPresSubgrupo update(DtoPresSubgrupo dtoPresSubgrupo, String accessToken);
    void delete(String codigo, String accessToken);
}