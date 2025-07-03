package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceActividad {
    DtoActividad find(Long id);

    Page<DtoActividad> findAll(Long proyectoId, Pageable pageable, Map<String, String> searchCriteria);
    List<DtoActividad> findAll(Long proyectoId);
    List<DtoActividad> findAllActivos(Long proyectoId);
    Page<DtoActividad> findAllActivos(Pageable pageable, Map <String, String> searchCriteria);
    Page<DtoActividad> findAllActivos(Long proyectoId, Pageable pageable, Map<String, String> searchCriteria);

    Page<DtoActividad> findByProyecto(Long proyectoId, Pageable pageable, Map<String, String> searchCriteria);

    DtoActividad save(DtoActividad dtoActividad, Long proyectoId, String accessToken);
    DtoActividad update(DtoActividad dtoActividad, Long proyectoId, String accessToken);
    void delete(Long id, String accessToken);
    DtoActividad crearActividadDefault(String accessToken, Long proyectoId);
}