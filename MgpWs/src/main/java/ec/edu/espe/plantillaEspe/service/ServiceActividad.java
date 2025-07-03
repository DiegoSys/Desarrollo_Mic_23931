package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoActividad;
import ec.edu.espe.plantillaEspe.dao.DaoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoActividad;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Actividad;
import ec.edu.espe.plantillaEspe.model.Proyecto;
import ec.edu.espe.plantillaEspe.service.IService.IServiceActividad;
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
public class ServiceActividad implements IServiceActividad {

    private final DaoActividad daoActividad;
    private final DaoProyecto daoProyecto;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceActividad(DaoActividad daoActividad,
                            DaoProyecto daoProyecto,
                            UserInfoService userInfoService) {
        this.daoActividad = daoActividad;
        this.daoProyecto = daoProyecto;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoActividad find(Long id) {
        if (id == null) throw new DataValidationException("El id de la actividad no puede ser nulo.");
        return daoActividad.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró una actividad con el id especificado."));
    }

    @Override
    public Page<DtoActividad> findAll(Long proyectoId, Pageable pageable, Map<String, String> searchCriteria) {
        if (proyectoId == null) throw new DataValidationException("El id del proyecto no puede ser nulo.");
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");

        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoActividad.findByProyecto_Id(proyectoId, pageable).map(this::convertToDto);
        } else {
            List<DtoActividad> actividades = daoActividad.findByProyecto_Id(proyectoId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return GenericSearchUtil.search(actividades, searchCriteria, pageable);
        }
    }

    @Override
    public List<DtoActividad> findAll(Long proyectoId) {
        if (proyectoId == null) throw new DataValidationException("El id del proyecto no puede ser nulo.");
        return daoActividad.findByProyecto_Id(proyectoId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DtoActividad> findAllActivos(Long proyectoId) {
        if (proyectoId == null) throw new DataValidationException("El id del proyecto no puede ser nulo.");
        return daoActividad.findByProyecto_IdAndEstado(proyectoId, Estado.A).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DtoActividad> findAllActivos(Long proyectoId, Pageable pageable, Map<String, String> searchCriteria) {
        if (proyectoId == null) throw new DataValidationException("El id del proyecto no puede ser nulo.");
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");

        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoActividad.findByProyecto_IdAndEstado(proyectoId, Estado.A, pageable).map(this::convertToDto);
        } else {
            List<DtoActividad> actividades = daoActividad.findByProyecto_IdAndEstado(proyectoId, Estado.A).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return GenericSearchUtil.search(actividades, searchCriteria, pageable);
        }
    }

    @Override
    public Page<DtoActividad> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");

        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoActividad.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } else {
            List<DtoActividad> actividades = daoActividad.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return GenericSearchUtil.search(actividades, searchCriteria, pageable);
        }
    }

    @Override
    public Page<DtoActividad> findByProyecto(Long proyectoId, Pageable pageable, Map<String, String> searchCriteria) {
        if (proyectoId == null) throw new DataValidationException("El id del proyecto no puede ser nulo.");
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");

        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoActividad.findByProyecto_Id(proyectoId, pageable).map(this::convertToDto);
        } else {
            List<DtoActividad> actividades = daoActividad.findByProyecto_Id(proyectoId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return GenericSearchUtil.search(actividades, searchCriteria, pageable);
        }
    }

    @Override
    @Transactional
    public DtoActividad save(DtoActividad dtoActividad, Long proyectoId, String accessToken) {
        validateDtoActividad(dtoActividad);
        String username = obtenerNombreUsuario(accessToken);

        Proyecto proyecto = daoProyecto.findById(proyectoId)
                .orElseThrow(() -> new DataValidationException("No se encontró el proyecto con el id especificado."));

        Optional<Actividad> existente = daoActividad.findByCodigoAndProyecto_Id(dtoActividad.getCodigo(), proyectoId);

        if (existente.isPresent()) {
            Actividad actividad = existente.get();
            if (Estado.A.equals(actividad.getEstado())) {
                throw new DataValidationException("Ya existe una actividad activa con el código especificado para este proyecto.");
            }
            actividad.setEstado(Estado.A);
            actividad.setDescripcion(dtoActividad.getDescripcion());
            actividad.setNombre(dtoActividad.getNombre());
            actividad.setAlineacion(TipoAlineacion.OPERATIVA);
            actividad.setFechaCreacion(new Date());
            actividad.setUsuarioCreacion(username);
            actividad.setProyecto(proyecto);
            actividad.setSubprograma(proyecto.getSubprograma());
            actividad.setPrograma(proyecto.getPrograma());
            return convertToDto(daoActividad.save(actividad));
        }

        Actividad actividad = crearActividad(dtoActividad, username);
        actividad.setProyecto(proyecto);
        actividad.setSubprograma(proyecto.getSubprograma());
        actividad.setPrograma(proyecto.getPrograma());
        actividad.setEstado(Estado.A);

        return convertToDto(daoActividad.save(actividad));
    }

    @Override
    @Transactional
    public DtoActividad update(DtoActividad dtoActividad, Long proyectoId, String accessToken) {
        validateDtoActividad(dtoActividad);
        String username = obtenerNombreUsuario(accessToken);

        Proyecto proyecto = daoProyecto.findById(proyectoId)
                .orElseThrow(() -> new DataValidationException("No se encontró el proyecto con el id especificado."));

        Actividad actividad = daoActividad.findByCodigoAndProyecto_Id(dtoActividad.getCodigo(), proyectoId)
                .orElseThrow(() -> new DataValidationException("La actividad con el código y proyecto especificados no existe."));

        if (Estado.I.equals(dtoActividad.getEstado()) && Estado.A.equals(actividad.getEstado())) {
            throw new DataValidationException("Para desactivar una actividad use el método delete()");
        }

        actividad.setFechaModificacion(new Date());
        actividad.setUsuarioModificacion(username);
        actividad.setEstado(Estado.A);
        actividad.setAlineacion(TipoAlineacion.OPERATIVA);
        actividad.setNombre(dtoActividad.getNombre());
        actividad.setDescripcion(dtoActividad.getDescripcion());
        actividad.setProyecto(proyecto);
        actividad.setSubprograma(proyecto.getSubprograma());
        actividad.setPrograma(proyecto.getPrograma());

        return convertToDto(daoActividad.save(actividad));
    }

    @Override
    @Transactional
    public void delete(Long id, String accessToken) {
        if (id == null) throw new DataValidationException("El id de la actividad no puede ser nulo.");
        Actividad actividad = daoActividad.findById(id)
                .orElseThrow(() -> new DataValidationException("No se encontró una actividad con el id especificado."));
        String username = obtenerNombreUsuario(accessToken);
        actividad.setEstado(Estado.I);
        actividad.setFechaModificacion(new Date());
        actividad.setUsuarioModificacion(username);
        daoActividad.save(actividad);
    }

    @Override
    @Transactional
    public DtoActividad crearActividadDefault(String accessToken, Long proyectoId) {
        Proyecto proyecto = daoProyecto.findById(proyectoId)
                .orElseThrow(() -> new DataValidationException("No se encontró el proyecto con el id especificado."));
        String username = obtenerNombreUsuario(accessToken);

        Optional<Actividad> existente = daoActividad.findByCodigoAndProyecto_Id("00", proyectoId);
        if (existente.isPresent()) {
            Actividad actividad = existente.get();
            if (Estado.A.equals(actividad.getEstado())) {
                throw new DataValidationException("Ya existe una actividad activa con el código 00 para este proyecto.");
            }
            actividad.setEstado(Estado.A);
            actividad.setFechaCreacion(new Date());
            actividad.setUsuarioCreacion(username);
            actividad.setAlineacion(TipoAlineacion.OPERATIVA);
            actividad.setDescripcion("N/A");
            actividad.setNombre("N/A");
            actividad.setProyecto(proyecto);
            actividad.setSubprograma(proyecto.getSubprograma());
            actividad.setPrograma(proyecto.getPrograma());

            return convertToDto(daoActividad.save(actividad));
        }

        Actividad nuevaActividad = new Actividad();
        nuevaActividad.setCodigo("00");
        nuevaActividad.setFechaCreacion(new Date());
        nuevaActividad.setUsuarioCreacion(username);
        nuevaActividad.setAlineacion(TipoAlineacion.OPERATIVA);
        nuevaActividad.setDescripcion("N/A");
        nuevaActividad.setNombre("N/A");
        nuevaActividad.setEstado(Estado.A);
        nuevaActividad.setProyecto(proyecto);
        nuevaActividad.setSubprograma(proyecto.getSubprograma());
        nuevaActividad.setPrograma(proyecto.getPrograma());

        return convertToDto(daoActividad.save(nuevaActividad));
    }

    // Métodos auxiliares
    private String obtenerNombreUsuario(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private void validateDtoActividad(DtoActividad dtoActividad) {
        if (dtoActividad == null) {
            throw new DataValidationException("DtoActividad no puede ser nulo.");
        }
        if (dtoActividad.getCodigo() == null || dtoActividad.getCodigo().isEmpty()) {
            throw new DataValidationException("El código de la actividad no puede ser nulo o vacío.");
        }
        if (dtoActividad.getDescripcion() == null || dtoActividad.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción es requerida.");
        }
        if (dtoActividad.getNombre() == null || dtoActividad.getNombre().isEmpty()) {
            throw new DataValidationException("El nombre es requerido.");
        }
    }

    private Actividad crearActividad(DtoActividad dto, String username) {
        Actividad actividad = new Actividad();
        actividad.setCodigo(dto.getCodigo());
        actividad.setFechaCreacion(new Date());
        actividad.setUsuarioCreacion(username);
        actividad.setDescripcion(dto.getDescripcion());
        actividad.setNombre(dto.getNombre());
        actividad.setEstado(Estado.A);
        actividad.setAlineacion(TipoAlineacion.OPERATIVA);
        return actividad;
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
        dto.setAlineacion(actividad.getAlineacion());
        dto.setFechaCreacion(actividad.getFechaCreacion());
        dto.setUsuarioCreacion(actividad.getUsuarioCreacion());
        dto.setFechaModificacion(actividad.getFechaModificacion());
        dto.setUsuarioModificacion(actividad.getUsuarioModificacion());

        if (actividad.getProyecto() != null) {
            dto.setProyectoId(actividad.getProyecto().getId());
        }
        if (actividad.getSubprograma() != null) {
            dto.setSubProgramaId(actividad.getSubprograma().getId());
        }
        if (actividad.getPrograma() != null) {
            dto.setProgramaId(actividad.getPrograma().getId());
        }

        return dto;
    }
}