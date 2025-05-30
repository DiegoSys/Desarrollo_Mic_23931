package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoActividad;
import ec.edu.espe.plantillaEspe.dto.DtoActividad;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Actividad;
import ec.edu.espe.plantillaEspe.service.IService.IServiceActividad;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ServiceActividad implements IServiceActividad {

    private final DaoActividad daoActividad;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceActividad(DaoActividad daoActividad,
                          UserInfoService userInfoService) {
        this.daoActividad = daoActividad;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoActividad find(String codigo) {
        validateCodigo(codigo);
        return daoActividad.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró una actividad con el código especificado."));
    }

    @Override
    public List<DtoActividad> findAll() {
        try {
            return daoActividad.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todas las actividades.", e);
        }
    }

    @Override
    public Page<DtoActividad> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoActividad.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las actividades paginadas.", e);
        }
    }

    @Override
    @Transactional
    public DtoActividad save(DtoActividad dtoActividad, String accessToken) {
        validateDtoActividad(dtoActividad);
        String username = obtenerNombreUsuario(accessToken);

        // Validar que no exista una actividad con el mismo código
        if (daoActividad.findByCodigo(dtoActividad.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe una actividad con el código especificado.");
        }

        Actividad actividad = new Actividad();
        actividad.setCodigo(dtoActividad.getCodigo());
        actividad.setNombre(dtoActividad.getNombre());
        actividad.setDescripcion(dtoActividad.getDescripcion());
        actividad.setEstado(dtoActividad.getEstado());
        actividad.setFechaCreacion(new Date());
        actividad.setUsuarioCreacion(username);

        return convertToDto(daoActividad.save(actividad));
    }

    @Override
    @Transactional
    public DtoActividad update(DtoActividad dtoActividad, String accessToken) {
        validateDtoActividad(dtoActividad);
        String username = obtenerNombreUsuario(accessToken);

        Actividad actividad = daoActividad.findByCodigo(dtoActividad.getCodigo())
                .orElseThrow(() -> new DataValidationException("No se encontró una actividad con el código especificado."));

        actividad.setNombre(dtoActividad.getNombre());
        actividad.setDescripcion(dtoActividad.getDescripcion());
        actividad.setEstado(dtoActividad.getEstado());
        actividad.setFechaModificacion(new Date());
        actividad.setUsuarioModificacion(username);

        return convertToDto(daoActividad.save(actividad));
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        validateCodigo(codigo);
        Actividad actividad = daoActividad.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró una actividad con el código especificado."));
        
        daoActividad.delete(actividad);
    }

    private void validarNuevoActividad(DtoActividad dtoActividad) {
        validateDtoActividad(dtoActividad);
        if (daoActividad.findByCodigo(dtoActividad.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe una actividad con el código especificado.");
        }
    }

    private String obtenerNombreUsuario(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código de la actividad no puede ser nulo o vacío.");
        }
    }

    private void validateDtoActividad(DtoActividad dtoActividad) {
        if (dtoActividad == null) {
            throw new DataValidationException("DtoActividad no puede ser nulo.");
        }
        validateCodigo(dtoActividad.getCodigo());
    }

    private DtoActividad convertToDto(Actividad actividad) {
        if (actividad == null) {
            return null;
        }
        DtoActividad dto = new DtoActividad();
        dto.setId(actividad.getId());
        dto.setCodigo(actividad.getCodigo());
        dto.setNombre(actividad.getNombre());
        dto.setDescripcion(actividad.getDescripcion());
        dto.setEstado(actividad.getEstado());
        dto.setFechaCreacion(actividad.getFechaCreacion());
        dto.setUsuarioCreacion(actividad.getUsuarioCreacion());
        dto.setFechaModificacion(actividad.getFechaModificacion());
        dto.setUsuarioModificacion(actividad.getUsuarioModificacion());
        return dto;
    }
}
