package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoTipoCampo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceTipoCampo {
    DtoTipoCampo find(String codigo);
    List<DtoTipoCampo> findAll();
    List<DtoTipoCampo> findAllActivos();
    Page<DtoTipoCampo> findAll(Pageable pageable);
    Page<DtoTipoCampo> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoTipoCampo save(DtoTipoCampo dtoTipoCampo, String accessToken);
    DtoTipoCampo update(DtoTipoCampo dtoTipoCampo, String accessToken);
    void delete(String codigo, String accessToken);
}