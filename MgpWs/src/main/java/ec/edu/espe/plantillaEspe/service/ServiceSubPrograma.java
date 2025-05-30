package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoSubPrograma;
import ec.edu.espe.plantillaEspe.dao.DaoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoSubPrograma;
import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.model.SubPrograma;
import ec.edu.espe.plantillaEspe.model.Proyecto;
import ec.edu.espe.plantillaEspe.service.IService.IServiceSubPrograma;
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
public class ServiceSubPrograma implements IServiceSubPrograma {

    private final DaoSubPrograma daoSubPrograma;
    private final DaoProyecto daoProyecto;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceSubPrograma(DaoSubPrograma daoSubPrograma,
                              DaoProyecto daoProyecto,
                              UserInfoService userInfoService) {
        this.daoSubPrograma = daoSubPrograma;
        this.daoProyecto = daoProyecto;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoSubPrograma find(String codigo) {
        validateCodigo(codigo);
        return daoSubPrograma.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un subprograma con el código especificado."));
    }

    @Override
    public List<DtoSubPrograma> findAll() {
        try {
            return daoSubPrograma.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los subprogramas.", e);
        }
    }

    @Override
    public Page<DtoSubPrograma> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoSubPrograma.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los subprogramas paginados.", e);
        }
    }

    @Override
    @Transactional
    public DtoSubPrograma save(DtoSubPrograma dtoSubPrograma, String accessToken) {
        validarNuevoSubPrograma(dtoSubPrograma);
        String username = obtenerNombreUsuario(accessToken);

        SubPrograma subPrograma = crearSubPrograma(dtoSubPrograma, username);
        vincularProyectos(subPrograma, dtoSubPrograma.getProyectos(), username);
        return convertToDto(daoSubPrograma.save(subPrograma));
    }

    @Override
    @Transactional
    public DtoSubPrograma update(DtoSubPrograma dtoSubPrograma, String accessToken) {
        if (dtoSubPrograma.getCodigo() == null || dtoSubPrograma.getCodigo().isEmpty()) {
            throw new DataValidationException("El código del subprograma no puede ser nulo o vacío.");
        }

        validateDtoSubPrograma(dtoSubPrograma);
        String username = obtenerNombreUsuario(accessToken);

        SubPrograma subPrograma = obtenerSubProgramaExistente(dtoSubPrograma.getCodigo());
        actualizarDatosBasicos(subPrograma, dtoSubPrograma, username);
        actualizarProyectosAsociados(subPrograma, dtoSubPrograma.getProyectos(), username);

        return convertToDto(daoSubPrograma.save(subPrograma));
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        validateCodigo(codigo);
        SubPrograma subPrograma = daoSubPrograma.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un subprograma con el código especificado."));

        try {
            daoSubPrograma.delete(subPrograma);
            List<Proyecto> proyectosList = subPrograma.getProyectos();
            for (Proyecto proyecto : proyectosList) {
                proyecto.setSubprograma(null);
                proyecto.setFechaModificacion(new Date());
                proyecto.setUsuarioModificacion(subPrograma.getUsuarioModificacion());
                daoProyecto.save(proyecto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el subprograma.", e);
        }
    }

    private void validarNuevoSubPrograma(DtoSubPrograma dtoSubPrograma) {
        validateDtoSubPrograma(dtoSubPrograma);
        if (daoSubPrograma.findByCodigo(dtoSubPrograma.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe un subprograma con el código especificado.");
        }
    }

    private String obtenerNombreUsuario(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del subprograma no puede ser nulo o vacío.");
        }
    }

    private void validateDtoSubPrograma(DtoSubPrograma dtoSubPrograma) {
        if (dtoSubPrograma == null) {
            throw new DataValidationException("DtoSubPrograma no puede ser nulo.");
        }
        validateCodigo(dtoSubPrograma.getCodigo());
    }

    private SubPrograma crearSubPrograma(DtoSubPrograma dto, String username) {
        SubPrograma subPrograma = new SubPrograma();
        subPrograma.setCodigo(dto.getCodigo());
        subPrograma.setFechaCreacion(new Date());
        subPrograma.setUsuarioCreacion(username);
        subPrograma.setEstado(dto.getEstado());
        subPrograma.setDescripcion(dto.getDescripcion());
        subPrograma.setNombre(dto.getNombre());
        return subPrograma;
    }

    private SubPrograma obtenerSubProgramaExistente(String codigo) {
        return daoSubPrograma.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException(
                        "El subprograma con el código especificado no existe."));
    }

    private void actualizarDatosBasicos(SubPrograma subPrograma,
                                        DtoSubPrograma dto,
                                        String username) {
        subPrograma.setFechaModificacion(new Date());
        subPrograma.setUsuarioModificacion(username);
        subPrograma.setEstado(dto.getEstado());
        subPrograma.setDescripcion(dto.getDescripcion());
        subPrograma.setNombre(dto.getNombre());
    }

    private DtoSubPrograma convertToDto(SubPrograma subPrograma) {
        if (subPrograma == null) {
            return null;
        }
        DtoSubPrograma dto = new DtoSubPrograma();
        dto.setId(subPrograma.getId());
        dto.setCodigo(subPrograma.getCodigo());
        dto.setFechaCreacion(subPrograma.getFechaCreacion());
        dto.setUsuarioCreacion(subPrograma.getUsuarioCreacion());
        dto.setFechaModificacion(subPrograma.getFechaModificacion());
        dto.setUsuarioModificacion(subPrograma.getUsuarioModificacion());
        dto.setEstado(subPrograma.getEstado());
        dto.setDescripcion(subPrograma.getDescripcion());
        dto.setNombre(subPrograma.getNombre());

        if (subPrograma.getProyectos() != null) {
            List<DtoProyecto> proyectos = subPrograma.getProyectos().stream()
                    .map(p -> {
                        DtoProyecto dtoProyecto = new DtoProyecto();
                        dtoProyecto.setId(p.getId());
                        dtoProyecto.setCodigo(p.getCodigo());
                        dtoProyecto.setNombre(p.getNombre());
                        dtoProyecto.setDescripcion(p.getDescripcion());
                        dtoProyecto.setEstado(p.getEstado());
                        return dtoProyecto;
                    })
                    .collect(Collectors.toList());
            dto.setProyectos(proyectos);
        }
        return dto;
    }

    private void vincularProyectos(SubPrograma subPrograma, List<DtoProyecto> dtosProyecto, String username) {
        if (dtosProyecto != null && !dtosProyecto.isEmpty()) {
            List<Proyecto> proyectos = obtenerProyectosExistentes(dtosProyecto);
            actualizarReferenciasProyectos(proyectos, subPrograma, username);
            subPrograma.setProyectos(proyectos);
        }
    }

    private List<Proyecto> obtenerProyectosExistentes(List<DtoProyecto> dtosProyecto) {
        return dtosProyecto.stream()
                .map(dto -> daoProyecto.findByCodigo(dto.getCodigo())
                        .orElseThrow(() -> new DataValidationException(
                                "No se encontró un proyecto con el código especificado.")))
                .collect(Collectors.toList());
    }

    private void actualizarProyectosAsociados(SubPrograma subPrograma,
                                              List<DtoProyecto> nuevosProyectos,
                                              String username) {
        List<Proyecto> proyectosActualizados = obtenerProyectosExistentes(nuevosProyectos);
        actualizarReferenciasProyectos(proyectosActualizados, subPrograma, username);
        desvincularProyectosEliminados(subPrograma.getProyectos(), proyectosActualizados, username);
        subPrograma.setProyectos(proyectosActualizados);
    }

    private void actualizarReferenciasProyectos(List<Proyecto> proyectos,
                                                SubPrograma subPrograma,
                                                String username) {
        for (Proyecto proyecto : proyectos) {
            proyecto.setSubprograma(subPrograma);
            proyecto.setFechaModificacion(new Date());
            proyecto.setUsuarioModificacion(username);
            daoProyecto.save(proyecto);
        }
    }

    private void desvincularProyectosEliminados(List<Proyecto> proyectosActuales,
                                                List<Proyecto> proyectosNuevos,
                                                String username) {
        for (Proyecto proyectoActual : proyectosActuales) {
            if (proyectoDebeSerDesvinculado(proyectoActual, proyectosNuevos)) {
                desvincularProyecto(proyectoActual, username);
            }
        }
    }

    private boolean proyectoDebeSerDesvinculado(Proyecto proyectoActual, List<Proyecto> proyectosNuevos) {
        return proyectosNuevos.stream()
                .noneMatch(proyectoNuevo -> proyectoNuevo.getCodigo().equals(proyectoActual.getCodigo()));
    }

    private void desvincularProyecto(Proyecto proyecto, String username) {
        proyecto.setSubprograma(null);
        proyecto.setFechaModificacion(new Date());
        proyecto.setUsuarioModificacion(username);
        daoProyecto.save(proyecto);
    }
}