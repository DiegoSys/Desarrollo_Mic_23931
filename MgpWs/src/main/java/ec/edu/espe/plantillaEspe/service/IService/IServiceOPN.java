package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoEje;
import ec.edu.espe.plantillaEspe.dto.DtoOPN;
import ec.edu.espe.plantillaEspe.model.OPN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceOPN {
    DtoOPN find(String codigo);
    Page<DtoOPN> findAll(Pageable pageable);
    List<DtoOPN> findAll();
    List<DtoOPN> findAllActivos();
    Page<DtoOPN> findAllActivos(Pageable pageable);
    DtoOPN save(DtoOPN dtoOPN, String accessToken);
    DtoOPN update(DtoOPN dtoOPN, String accessToken);
    void delete(String codigo, String accessToken);

}