package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoProyecto;
import ec.edu.espe.plantillaEspe.dao.DaoSubPrograma;
import ec.edu.espe.plantillaEspe.dao.DaoPrograma;
import ec.edu.espe.plantillaEspe.dao.DaoActividad;
import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoActividad;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Proyecto;
import ec.edu.espe.plantillaEspe.model.SubPrograma;
import ec.edu.espe.plantillaEspe.model.Programa;
import ec.edu.espe.plantillaEspe.model.Actividad;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProyecto;
import ec.edu.espe.plantillaEspe.util.GenericSearchUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceProyecto implements IServiceProyecto {

    private final DaoProyecto daoProyecto;
    private final DaoSubPrograma daoSubPrograma;
    private final DaoPrograma daoPrograma;
    private final DaoActividad daoActividad;
    private final UserInfoService userInfoService;
    private final ServiceActividad serviceActividad;

    @Autowired
    public ServiceProyecto(DaoProyecto daoProyecto,
                           DaoSubPrograma daoSubPrograma,
                           DaoPrograma daoPrograma,
                           DaoActividad daoActividad,
                           UserInfoService userInfoService,
                           ServiceActividad serviceActividad) {
        this.daoProyecto = daoProyecto;
        this.daoSubPrograma = daoSubPrograma;
        this.daoPrograma = daoPrograma;
        this.daoActividad = daoActividad;
        this.userInfoService = userInfoService;
        this.serviceActividad = serviceActividad;
    }

    @Override
    public DtoProyecto find(Long id) {
        if (id == null) throw new DataValidationException("El id del proyecto no puede ser nulo.");
        return daoProyecto.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un proyecto con el id especificado."));
    }

    @Override
    public Page<DtoProyecto> findAll(Long programaId, Long subProgramaId, Pageable pageable, Map<String, String> searchCriteria) {
        List<DtoProyecto> proyectos = daoProyecto.findAll().stream()
            .filter(p -> p.getPrograma().getId().equals(programaId) && 
                        p.getSubprograma().getId().equals(subProgramaId))
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return GenericSearchUtil.search(proyectos, searchCriteria, pageable);
    }

    @Override
    public List<DtoProyecto> findAll(Long programaId, Long subProgramaId) {
        return daoProyecto.findAll().stream()
            .filter(p -> p.getPrograma().getId().equals(programaId) && 
                        p.getSubprograma().getId().equals(subProgramaId))
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<DtoProyecto> findAllActivos(Long programaId, Long subProgramaId) {
        return daoProyecto.findByEstado(Estado.A).stream()
            .filter(p -> p.getPrograma().getId().equals(programaId) && 
                        p.getSubprograma().getId().equals(subProgramaId))
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    public Page<DtoProyecto> findAllActivos(Long programaId, Long subProgramaId, Pageable pageable, Map<String, String> searchCriteria) {
        List<DtoProyecto> proyectos = daoProyecto.findByEstado(Estado.A).stream()
            .filter(p -> p.getPrograma().getId().equals(programaId) && 
                        p.getSubprograma().getId().equals(subProgramaId))
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return GenericSearchUtil.search(proyectos, searchCriteria, pageable);
    }

    @Override
    public Page<DtoProyecto> findByProgramaAndSubprograma(Long programaId, Long subProgramaId, Pageable pageable, Map<String, String> searchCriteria) {
        if (programaId == null || subProgramaId == null)
            throw new DataValidationException("Los IDs de programa y subprograma son requeridos.");
        if (pageable == null)
            throw new DataValidationException("Los parámetros de paginación son requeridos.");

        //log de searchCriteria
        System.out.println("Search Criteria: " + searchCriteria);
    
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            // Sin filtros: consulta paginada directa a la base de datos
            return daoProyecto.findBySubprograma_IdAndPrograma_IdAndEstado(subProgramaId, programaId, Estado.A, pageable)
                    .map(this::convertToDto);
        } else {
            // Con filtros: filtra en memoria usando GenericSearchUtil
            List<DtoProyecto> proyectos = daoProyecto.findAll().stream()
                .filter(p -> p.getPrograma().getId().equals(programaId) &&
                             p.getSubprograma().getId().equals(subProgramaId) &&
                             p.getEstado() == Estado.A)
                .map(this::convertToDto)
                .collect(Collectors.toList());
            return GenericSearchUtil.search(proyectos, searchCriteria, pageable);
        }
    }

    @Override
    @Transactional
    public DtoProyecto save(DtoProyecto dtoProyecto, Long programaId, Long subProgramaId, String accessToken) {
        validateDtoProyecto(dtoProyecto);
        String username = obtenerNombreUsuario(accessToken);

        Programa programa = daoPrograma.findById(programaId)
                .orElseThrow(() -> new DataValidationException("No se encontró el programa con el id especificado."));

        SubPrograma subPrograma = daoSubPrograma.findById(subProgramaId)
                .orElseThrow(() -> new DataValidationException("No se encontró el subprograma padre especificado."));

        Optional<Proyecto> existente = daoProyecto.findByCodigoAndSubprograma_IdAndPrograma_Id(
            dtoProyecto.getCodigo(), subProgramaId, programaId
        );

        if (existente.isPresent()) {
            Proyecto proyecto = existente.get();
            if (Estado.A.equals(proyecto.getEstado())) {
                throw new DataValidationException("Ya existe un proyecto activo con el código especificado para este subprograma.");
            }
            proyecto.setEstado(Estado.A);
            proyecto.setDescripcion(dtoProyecto.getDescripcion());
            proyecto.setNombre(dtoProyecto.getNombre());
            proyecto.setAlineacion(TipoAlineacion.OPERATIVA);
            proyecto.setFechaCreacion(new Date());
            proyecto.setUsuarioCreacion(username);
            proyecto.setSubprograma(subPrograma);
            proyecto.setPrograma(programa);

            vincularActividades(proyecto, dtoProyecto.getActividades(), username, programa, subPrograma);
            return convertToDto(daoProyecto.save(proyecto));
        }

        Proyecto proyecto = crearProyecto(dtoProyecto, username);
        proyecto.setSubprograma(subPrograma);
        proyecto.setPrograma(programa);
        proyecto.setEstado(Estado.A);
        vincularActividades(proyecto, dtoProyecto.getActividades(), username, programa, subPrograma);

        return convertToDto(daoProyecto.save(proyecto));
    }

@Transactional
public DtoProyecto update(DtoProyecto dtoProyecto, Long proyectoId, String accessToken) {
    validateDtoProyecto(dtoProyecto);
    String username = obtenerNombreUsuario(accessToken);

    Proyecto proyecto = daoProyecto.findById(proyectoId)
            .orElseThrow(() -> new DataValidationException("El proyecto con el id especificado no existe."));

    if (Estado.I.equals(dtoProyecto.getEstado()) && Estado.A.equals(proyecto.getEstado())) {
        throw new DataValidationException("Para desactivar un proyecto use el método delete()");
    }

    actualizarDatosBasicos(proyecto, dtoProyecto, username);

    // Si necesitas actualizar subprograma o programa, puedes hacerlo aquí usando los ids del dto
    if (dtoProyecto.getSubProgramaId() != null) {
        SubPrograma subPrograma = daoSubPrograma.findById(dtoProyecto.getSubProgramaId())
                .orElseThrow(() -> new DataValidationException("No se encontró el subprograma padre especificado."));
        proyecto.setSubprograma(subPrograma);
    }
    if (dtoProyecto.getProgramaId() != null) {
        Programa programa = daoPrograma.findById(dtoProyecto.getProgramaId())
                .orElseThrow(() -> new DataValidationException("No se encontró el programa con el id especificado."));
        proyecto.setPrograma(programa);
    }

    return convertToDto(daoProyecto.save(proyecto));
}

    @Override
    @Transactional
    public void delete(Long id, String accessToken) {
        if (id == null) throw new DataValidationException("El id del proyecto no puede ser nulo.");
        Proyecto proyecto = daoProyecto.findById(id)
                .orElseThrow(() -> new DataValidationException("No se encontró un proyecto con el id especificado."));

        String username = obtenerNombreUsuario(accessToken);

        proyecto.setEstado(Estado.I);
        proyecto.setFechaModificacion(new Date());
        proyecto.setUsuarioModificacion(username);
        daoProyecto.save(proyecto);

        // Desvincular actividades asociadas
        if (proyecto.getActividades() != null) {
            for (Actividad actividad : proyecto.getActividades()) {
                serviceActividad.delete(actividad.getId(), accessToken);
            }
        }
    }

    @Override
    @Transactional
    public DtoProyecto crearProyectoDefault(String accessToken, Long programaId, Long subProgramaId) {
        Programa programa = daoPrograma.findById(programaId)
                .orElseThrow(() -> new DataValidationException("No se encontró el programa con el id especificado."));

        SubPrograma subPrograma = daoSubPrograma.findById(subProgramaId)
                .orElseThrow(() -> new DataValidationException("No se encontró el subprograma con el id especificado."));

        String username = obtenerNombreUsuario(accessToken);

        Optional<Proyecto> existente = daoProyecto.findByCodigoAndSubprograma_IdAndPrograma_Id("00", subProgramaId, programaId);
        if (existente.isPresent()) {
            Proyecto proyecto = existente.get();
            if (Estado.A.equals(proyecto.getEstado())) {
                throw new DataValidationException("Ya existe un proyecto activo con el código 00 para este subprograma.");
            }
            proyecto.setEstado(Estado.A);
            proyecto.setFechaCreacion(new Date());
            proyecto.setUsuarioCreacion(username);
            proyecto.setAlineacion(TipoAlineacion.OPERATIVA);
            proyecto.setDescripcion("N/A");
            proyecto.setNombre("N/A");
            proyecto.setSubprograma(subPrograma);
            proyecto.setPrograma(programa);

            return convertToDto(daoProyecto.save(proyecto));
        }

        Proyecto nuevoProyecto = new Proyecto();
        nuevoProyecto.setCodigo("00");
        nuevoProyecto.setFechaCreacion(new Date());
        nuevoProyecto.setUsuarioCreacion(username);
        nuevoProyecto.setAlineacion(TipoAlineacion.OPERATIVA);
        nuevoProyecto.setDescripcion("N/A");
        nuevoProyecto.setNombre("N/A");
        nuevoProyecto.setEstado(Estado.A);
        nuevoProyecto.setSubprograma(subPrograma);
        nuevoProyecto.setPrograma(programa);

        return convertToDto(daoProyecto.save(nuevoProyecto));
    }

    // Métodos auxiliares
    private String obtenerNombreUsuario(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private void validateDtoProyecto(DtoProyecto dtoProyecto) {
        if (dtoProyecto == null) {
            throw new DataValidationException("DtoProyecto no puede ser nulo.");
        }
        if (dtoProyecto.getCodigo() == null || dtoProyecto.getCodigo().isEmpty()) {
            throw new DataValidationException("El código del proyecto no puede ser nulo o vacío.");
        }
        if (dtoProyecto.getDescripcion() == null || dtoProyecto.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción es requerida.");
        }
        if (dtoProyecto.getNombre() == null || dtoProyecto.getNombre().isEmpty()) {
            throw new DataValidationException("El nombre es requerido.");
        }
    }

    private Proyecto crearProyecto(DtoProyecto dto, String username) {
        Proyecto proyecto = new Proyecto();
        proyecto.setCodigo(dto.getCodigo());
        proyecto.setFechaCreacion(new Date());
        proyecto.setUsuarioCreacion(username);
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setNombre(dto.getNombre());
        proyecto.setEstado(Estado.A);
        proyecto.setAlineacion(TipoAlineacion.OPERATIVA);
        return proyecto;
    }

    private void actualizarDatosBasicos(Proyecto proyecto,
                                        DtoProyecto dto,
                                        String username) {
        proyecto.setFechaModificacion(new Date());
        proyecto.setUsuarioModificacion(username);
        proyecto.setEstado(Estado.A);
        proyecto.setAlineacion(TipoAlineacion.OPERATIVA);
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setNombre(dto.getNombre());
    }

    private void vincularActividades(Proyecto proyecto, List<DtoActividad> dtosActividad, String username, Programa programa, SubPrograma subPrograma) {
        List<Actividad> actividades = obtenerActividadesExistentes(dtosActividad, proyecto, programa, subPrograma, username);
        proyecto.setActividades(actividades);
    }

    private List<Actividad> obtenerActividadesExistentes(List<DtoActividad> dtosActividad, Proyecto proyecto, Programa programa, SubPrograma subPrograma, String username) {
        if (dtosActividad == null) return List.of();
        return dtosActividad.stream()
                .map(dto -> {
                    Optional<Actividad> actividadOpt = daoActividad.findByCodigoAndProyecto_Id(
                            dto.getCodigo(),
                            proyecto.getId()
                    );
                    Actividad actividad = actividadOpt.orElseGet(() -> {
                        Actividad nueva = new Actividad();
                        nueva.setCodigo(dto.getCodigo());
                        nueva.setNombre(dto.getNombre());
                        nueva.setDescripcion(dto.getDescripcion());
                        nueva.setEstado(dto.getEstado());
                        nueva.setAlineacion(dto.getAlineacion());
                        nueva.setUsuarioCreacion(username);
                        nueva.setFechaCreacion(new Date());
                        nueva.setProyecto(proyecto);
                        nueva.setSubprograma(subPrograma);
                        nueva.setPrograma(programa);
                        return nueva;
                    });
                    actividad.setProyecto(proyecto);
                    actividad.setSubprograma(subPrograma);
                    actividad.setPrograma(programa);
                    actividad.setNombre(dto.getNombre());
                    actividad.setDescripcion(dto.getDescripcion());
                    actividad.setEstado(dto.getEstado());
                    actividad.setAlineacion(dto.getAlineacion());
                    actividad.setUsuarioModificacion(username);
                    actividad.setFechaModificacion(new Date());
                    return daoActividad.save(actividad);
                })
                .collect(Collectors.toList());
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
        dto.setAlineacion(proyecto.getAlineacion());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setNombre(proyecto.getNombre());

        if (proyecto.getSubprograma() != null) {
            dto.setSubProgramaId(proyecto.getSubprograma().getId());
        }
        if (proyecto.getPrograma() != null) {
            dto.setProgramaId(proyecto.getPrograma().getId());
        }

        if (proyecto.getActividades() != null) {
            List<DtoActividad> actividades = proyecto.getActividades().stream()
                    .map(a -> {
                        DtoActividad dtoActividad = new DtoActividad();
                        dtoActividad.setId(a.getId());
                        dtoActividad.setCodigo(a.getCodigo());
                        dtoActividad.setNombre(a.getNombre());
                        dtoActividad.setDescripcion(a.getDescripcion());
                        dtoActividad.setEstado(a.getEstado());
                        dtoActividad.setAlineacion(a.getAlineacion());
                        dtoActividad.setProyectoId(proyecto.getId());
                        dtoActividad.setSubProgramaId(proyecto.getSubprograma() != null ? proyecto.getSubprograma().getId() : null);
                        dtoActividad.setProgramaId(proyecto.getPrograma() != null ? proyecto.getPrograma().getId() : null);
                        return dtoActividad;
                    })
                    .collect(Collectors.toList());
            dto.setActividades(actividades);
        }
        return dto;
    }
}