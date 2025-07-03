package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceProyecto {
    DtoProyecto find(Long id);

    Page<DtoProyecto> findAll(Long programaId, Long subProgramaId, Pageable pageable, Map<String, String> searchCriteria);
    List<DtoProyecto> findAll(Long programaId, Long subProgramaId);
    List<DtoProyecto> findAllActivos(Long programaId, Long subProgramaId);
    Page<DtoProyecto> findAllActivos(Long programaId, Long subProgramaId, Pageable pageable, Map<String, String> searchCriteria);

    Page<DtoProyecto> findByProgramaAndSubprograma(Long programaId, Long subProgramaId, Pageable pageable, Map<String, String> searchCriteria);

    DtoProyecto save(DtoProyecto dtoProyecto, Long programaId, Long subProgramaId, String accessToken);
    DtoProyecto update(DtoProyecto dtoProyecto, Long Id, String accessToken);
    void delete(Long id, String accessToken);
    DtoProyecto crearProyectoDefault(String accessToken, Long programaId, Long subProgramaId);
}