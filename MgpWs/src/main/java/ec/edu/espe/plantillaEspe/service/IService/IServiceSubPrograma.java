package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoSubPrograma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceSubPrograma {
    DtoSubPrograma find(Long id);
    DtoSubPrograma findByCodigoAndProgramaId(String codigo, Long programaId);

    Page<DtoSubPrograma> findAll(Pageable pageable, Map<String, String> searchCriteria);
    List<DtoSubPrograma> findAll();
    List<DtoSubPrograma> findAllActivos();
    Page<DtoSubPrograma> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    Page<DtoSubPrograma> findByProgramaIdAndEstado(Long programaId, Pageable pageable, Map<String, String> searchCriteria);

    DtoSubPrograma save(DtoSubPrograma dtoSubPrograma, String accessToken);
    DtoSubPrograma update(DtoSubPrograma dtoSubPrograma, Long id, String accessToken);
    void delete(Long id, String accessToken);
    DtoSubPrograma crearSubProgramaDefault(String accessToken, Long programaId);
}