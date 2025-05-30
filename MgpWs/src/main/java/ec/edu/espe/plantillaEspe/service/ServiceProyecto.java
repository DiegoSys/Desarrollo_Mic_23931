package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoProyecto;
import ec.edu.espe.plantillaEspe.dao.DaoActividad;
import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoActividad;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.model.Proyecto;
import ec.edu.espe.plantillaEspe.model.Actividad;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProyecto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceProyecto implements IServiceProyecto {

    private final DaoProyecto daoProyecto;
    private final DaoActividad daoActividad;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceProyecto(DaoProyecto daoProyecto,
                         DaoActividad daoActividad,
                         UserInfoService userInfoService) {
        this.daoProyecto = daoProyecto;
        this.daoActividad = daoActividad;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoProyecto find(String codigo) {
        validateCodigo(codigo);
        return daoProyecto.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un proyecto con el código especificado."));
    }

    @Override
    public List<DtoProyecto> findAll() {
        try {
            return daoProyecto.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los proyectos.", e);
        }
    }

    @Override
    public Page<DtoProyecto> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoProyecto.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los proyectos paginados.", e);
        }
    }

    @Override
    @Transactional
    public DtoProyecto save(DtoProyecto dtoProyecto, String accessToken) {
        validarNuevoProyecto(dtoProyecto);
        String username = obtenerNombreUsuario(accessToken);

        Proyecto proyecto = crearProyecto(dtoProyecto, username);
        vincularActividades(proyecto, dtoProyecto.getActividades(), username);
        return convertToDto(daoProyecto.save(proyecto));
    }

    @Override
    @Transactional
    public DtoProyecto update(DtoProyecto dtoProyecto, String accessToken) {
        if (dtoProyecto.getCodigo() == null || dtoProyecto.getCodigo().isEmpty()) {
            throw new DataValidationException("El código del proyecto no puede ser nulo o vacío.");
        }

        validateDtoProyecto(dtoProyecto);
        String username = obtenerNombreUsuario(accessToken);

        Proyecto proyecto = obtenerProyectoExistente(dtoProyecto.getCodigo());
        actualizarDatosBasicos(proyecto, dtoProyecto, username);
        actualizarActividadesAsociadas(proyecto, dtoProyecto.getActividades(), username);

        return convertToDto(daoProyecto.save(proyecto));
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        validateCodigo(codigo);
        Proyecto proyecto = daoProyecto.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un proyecto con el código especificado."));

        try {
            daoProyecto.delete(proyecto);
            List<Actividad> actividadesList = proyecto.getActividades();
            for (Actividad actividad : actividadesList) {
                actividad.setProyecto(null);
                actividad.setFechaModificacion(new Date());
                actividad.setUsuarioModificacion(proyecto.getUsuarioModificacion());
                daoActividad.save(actividad);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el proyecto.", e);
        }
    }

    private void validarNuevoProyecto(DtoProyecto dtoProyecto) {
        validateDtoProyecto(dtoProyecto);
        if (daoProyecto.findByCodigo(dtoProyecto.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe un proyecto con el código especificado.");
        }
    }

    private String obtenerNombreUsuario(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del proyecto no puede ser nulo o vacío.");
        }
    }

    private void validateDtoProyecto(DtoProyecto dtoProyecto) {
        if (dtoProyecto == null) {
            throw new DataValidationException("DtoProyecto no puede ser nulo.");
        }
        validateCodigo(dtoProyecto.getCodigo());
    }

    private Proyecto crearProyecto(DtoProyecto dto, String username) {
        Proyecto proyecto = new Proyecto();
        proyecto.setCodigo(dto.getCodigo());
        proyecto.setFechaCreacion(new Date());
        proyecto.setUsuarioCreacion(username);
        proyecto.setEstado(dto.getEstado());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setNombre(dto.getNombre());
        return proyecto;
    }

    private Proyecto obtenerProyectoExistente(String codigo) {
        return daoProyecto.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException(
                        "El proyecto con el código especificado no existe."));
    }

    private void actualizarDatosBasicos(Proyecto proyecto,
                                      DtoProyecto dto,
                                      String username) {
        proyecto.setFechaModificacion(new Date());
        proyecto.setUsuarioModificacion(username);
        proyecto.setEstado(dto.getEstado());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setNombre(dto.getNombre());
    }

    private DtoProyecto convertToDto(Proyecto proyecto) {
        if (proyecto == null) {
            return null;
        }
        DtoProyecto dto = new DtoProyecto();
        dto.setId(proyecto.getId());
        dto.setCodigo(proyecto.getCodigo());
        dto.setFechaCreacion(proyecto.getFechaCreacion());
        dto.setUsuarioCreacion(proyecto.getUsuarioCreacion());
        dto.setFechaModificacion(proyecto.getFechaModificacion());
        dto.setUsuarioModificacion(proyecto.getUsuarioModificacion());
        dto.setEstado(proyecto.getEstado());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setNombre(proyecto.getNombre());

        if (proyecto.getActividades() != null) {
            List<DtoActividad> actividades = proyecto.getActividades().stream()
                    .map(a -> {
                        DtoActividad dtoActividad = new DtoActividad();
                        dtoActividad.setId(a.getId());
                        dtoActividad.setCodigo(a.getCodigo());
                        dtoActividad.setNombre(a.getNombre());
                        dtoActividad.setDescripcion(a.getDescripcion());
                        dtoActividad.setEstado(a.getEstado());
                        return dtoActividad;
                    })
                    .collect(Collectors.toList());
            dto.setActividades(actividades);
        }
        return dto;
    }

    private void vincularActividades(Proyecto proyecto, List<DtoActividad> dtosActividad, String username) {
        if (dtosActividad != null && !dtosActividad.isEmpty()) {
            List<Actividad> actividades = obtenerActividadesExistentes(dtosActividad);
            actualizarReferenciasActividades(actividades, proyecto, username);
            proyecto.setActividades(actividades);
        }
    }

    private List<Actividad> obtenerActividadesExistentes(List<DtoActividad> dtosActividad) {
        return dtosActividad.stream()
                .map(dto -> daoActividad.findByCodigo(dto.getCodigo())
                        .orElseThrow(() -> new DataValidationException(
                                "No se encontró una actividad con el código especificado.")))
                .collect(Collectors.toList());
    }

    private void actualizarActividadesAsociadas(Proyecto proyecto,
                                              List<DtoActividad> nuevasActividades,
                                              String username) {
        List<Actividad> actividadesActualizadas = obtenerActividadesExistentes(nuevasActividades);
        actualizarReferenciasActividades(actividadesActualizadas, proyecto, username);
        desvincularActividadesEliminadas(proyecto.getActividades(), actividadesActualizadas, username);
        proyecto.setActividades(actividadesActualizadas);
    }

    private void actualizarReferenciasActividades(List<Actividad> actividades,
                                             Proyecto proyecto,
                                             String username) {
        for (Actividad actividad : actividades) {
            actividad.setProyecto(proyecto);
            actividad.setFechaModificacion(new Date());
            actividad.setUsuarioModificacion(username);
            daoActividad.save(actividad);
        }
    }

    private void desvincularActividadesEliminadas(List<Actividad> actividadesActuales,
                                             List<Actividad> actividadesNuevas,
                                             String username) {
        for (Actividad actividadActual : actividadesActuales) {
            if (actividadDebeSerDesvinculada(actividadActual, actividadesNuevas)) {
                desvincularActividad(actividadActual, username);
            }
        }
    }

    private boolean actividadDebeSerDesvinculada(Actividad actividadActual, List<Actividad> actividadesNuevas) {
        return actividadesNuevas.stream()
                .noneMatch(actividadNueva -> actividadNueva.getCodigo().equals(actividadActual.getCodigo()));
    }

    private void desvincularActividad(Actividad actividad, String username) {
        actividad.setProyecto(null);
        actividad.setFechaModificacion(new Date());
        actividad.setUsuarioModificacion(username);
        daoActividad.save(actividad);
    }
}
