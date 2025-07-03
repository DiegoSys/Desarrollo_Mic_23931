package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoSubPrograma;
import ec.edu.espe.plantillaEspe.dao.DaoPrograma;
import ec.edu.espe.plantillaEspe.dao.DaoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoSubPrograma;
import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.SubPrograma;
import ec.edu.espe.plantillaEspe.model.Programa;
import ec.edu.espe.plantillaEspe.model.Proyecto;
import ec.edu.espe.plantillaEspe.service.IService.IServiceSubPrograma;
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
public class ServiceSubPrograma implements IServiceSubPrograma {

    private final DaoSubPrograma daoSubPrograma;
    private final DaoPrograma daoPrograma;
    private final DaoProyecto daoProyecto;
    private final UserInfoService userInfoService;
    private final ServiceProyecto serviceProyecto;

    @Autowired
    public ServiceSubPrograma(DaoSubPrograma daoSubPrograma,
                              DaoPrograma daoPrograma,
                              DaoProyecto daoProyecto,
                              UserInfoService userInfoService,
                              ServiceProyecto serviceProyecto) {
        this.daoSubPrograma = daoSubPrograma;
        this.daoPrograma = daoPrograma;
        this.daoProyecto = daoProyecto;
        this.userInfoService = userInfoService;
        this.serviceProyecto = serviceProyecto;
    }

    @Override
    public DtoSubPrograma find(Long id) {
        if (id == null) throw new DataValidationException("El id del subprograma no puede ser nulo.");
        return daoSubPrograma.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un subprograma con el id especificado."));
    }

    @Override
    public DtoSubPrograma findByCodigoAndProgramaId(String codigo, Long programaId) {
        if (codigo == null || codigo.isEmpty()) throw new DataValidationException("El código no puede ser nulo o vacío.");
        if (programaId == null) throw new DataValidationException("El id del programa no puede ser nulo.");
        return daoSubPrograma.findByCodigoAndPrograma_Id(codigo, programaId)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un subprograma con el código y programa especificados."));
    }

    @Override
    public List<DtoSubPrograma> findAll() {
        return daoSubPrograma.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<DtoSubPrograma> findAllActivos() {
        return daoSubPrograma.findByEstado(Estado.A).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Page<DtoSubPrograma> findAll(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoSubPrograma.findAll(pageable).map(this::convertToDto);
        } else {
            List<DtoSubPrograma> subProgramas = daoSubPrograma.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return GenericSearchUtil.search(subProgramas, searchCriteria, pageable);
        }
    }

    @Override
    public Page<DtoSubPrograma> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoSubPrograma.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } else {
            List<DtoSubPrograma> subProgramas = daoSubPrograma.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return GenericSearchUtil.search(subProgramas, searchCriteria, pageable);
        }
    }

    /**
     * Busca subprogramas por ID de programa y estado.
     * @param programaId ID del programa al que pertenecen los subprogramas.
     * @param pageable Parámetros de paginación.
     * @param searchCriteria Criterios de búsqueda adicionales.
     * @return Una página de subprogramas que cumplen con los criterios.
     */
    @Override
    public Page<DtoSubPrograma> findByProgramaIdAndEstado(Long programaId, Pageable pageable, Map<String, String> searchCriteria) {
        if (programaId == null) throw new DataValidationException("El id del programa no puede ser nulo.");
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");
        Programa programa = daoPrograma.findById(programaId)
                .orElseThrow(() -> new DataValidationException("No se encontró un programa con el id especificado."));
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoSubPrograma.findByProgramaAndEstado(programa, Estado.A, pageable)
                    .map(this::convertToDto);
        } else {
            List<DtoSubPrograma> subProgramas = daoSubPrograma.findByProgramaAndEstado(programa, Estado.A, pageable).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return GenericSearchUtil.search(subProgramas, searchCriteria, pageable);
        }
    }

    @Override
    @Transactional
    public DtoSubPrograma save(DtoSubPrograma dtoSubPrograma, String accessToken) {
        validateDtoSubPrograma(dtoSubPrograma);
        String username = obtenerNombreUsuario(accessToken);

        Programa programa = daoPrograma.findById(dtoSubPrograma.getProgramaId())
                .orElseThrow(() -> new DataValidationException("No se encontró el programa padre especificado."));

        Optional<SubPrograma> existente = daoSubPrograma.findByCodigoAndPrograma_Id(dtoSubPrograma.getCodigo(), programa.getId());

        if (existente.isPresent()) {
            SubPrograma subPrograma = existente.get();
            if (Estado.A.equals(subPrograma.getEstado())) {
                throw new DataValidationException("Ya existe un subprograma activo con el código especificado para este programa.");
            }
            subPrograma.setEstado(Estado.A);
            subPrograma.setDescripcion(dtoSubPrograma.getDescripcion());
            subPrograma.setNombre(dtoSubPrograma.getNombre());
            subPrograma.setAlineacion(TipoAlineacion.OPERATIVA);
            subPrograma.setFechaCreacion(new Date());
            subPrograma.setUsuarioCreacion(username);
            subPrograma.setPrograma(programa);
            vincularProyectos(subPrograma, dtoSubPrograma.getProyectos(), username, programa);
            return convertToDto(daoSubPrograma.save(subPrograma));
        }

        SubPrograma subPrograma = crearSubPrograma(dtoSubPrograma, username);
        subPrograma.setPrograma(programa);
        subPrograma.setEstado(Estado.A);
        vincularProyectos(subPrograma, dtoSubPrograma.getProyectos(), username, programa);

        return convertToDto(daoSubPrograma.save(subPrograma));
    }

    @Override
    @Transactional
    public DtoSubPrograma crearSubProgramaDefault(String accessToken, Long programaId) {
        if (programaId == null) throw new DataValidationException("El id del programa no puede ser nulo.");
        Programa programa = daoPrograma.findById(programaId)
                .orElseThrow(() -> new DataValidationException("No se encontró un programa con el id especificado."));
        String username = obtenerNombreUsuario(accessToken);

        Optional<SubPrograma> existente = daoSubPrograma.findByCodigoAndPrograma_Id("00", programaId);
        if (existente.isPresent()) {
            SubPrograma subPrograma = existente.get();
            if (Estado.A.equals(subPrograma.getEstado())) {
                throw new DataValidationException("Ya existe un subprograma activo con el código 00 para este programa.");
            }
            subPrograma.setEstado(Estado.A);
            subPrograma.setFechaCreacion(new Date());
            subPrograma.setUsuarioCreacion(username);
            subPrograma.setAlineacion(TipoAlineacion.OPERATIVA);
            subPrograma.setDescripcion("N/A");
            subPrograma.setNombre("N/A");
            subPrograma.setPrograma(programa);
            return convertToDto(daoSubPrograma.save(subPrograma));
        }

        SubPrograma nuevoSubPrograma = new SubPrograma();
        nuevoSubPrograma.setCodigo("00");
        nuevoSubPrograma.setFechaCreacion(new Date());
        nuevoSubPrograma.setUsuarioCreacion(username);
        nuevoSubPrograma.setAlineacion(TipoAlineacion.OPERATIVA);
        nuevoSubPrograma.setDescripcion("N/A");
        nuevoSubPrograma.setNombre("N/A");
        nuevoSubPrograma.setEstado(Estado.A);
        nuevoSubPrograma.setPrograma(programa);

        return convertToDto(daoSubPrograma.save(nuevoSubPrograma));
    }

    @Override
    @Transactional
    public DtoSubPrograma update(DtoSubPrograma dtoSubPrograma, Long id, String accessToken) {
        validateDtoSubPrograma(dtoSubPrograma);
        String username = obtenerNombreUsuario(accessToken);
    
        // Buscar el subprograma solo por el id recibido como argumento
        SubPrograma subPrograma = daoSubPrograma.findById(id)
                .orElseThrow(() -> new DataValidationException("El subprograma con el id especificado no existe."));
    
        // Si necesitas actualizar el programa, puedes hacerlo aquí usando el id del DTO
        if (dtoSubPrograma.getProgramaId() != null) {
            Programa programa = daoPrograma.findById(dtoSubPrograma.getProgramaId())
                    .orElseThrow(() -> new DataValidationException("No se encontró el programa padre especificado."));
            subPrograma.setPrograma(programa);
        }
    
        if (Estado.I.equals(dtoSubPrograma.getEstado()) && Estado.A.equals(subPrograma.getEstado())) {
            throw new DataValidationException("Para desactivar un subprograma use el método delete()");
        }
    
        actualizarDatosBasicos(subPrograma, dtoSubPrograma, username);
    

        return convertToDto(daoSubPrograma.save(subPrograma));
    }

    @Override
    @Transactional
    public void delete(Long id, String accessToken) {
        if (id == null) throw new DataValidationException("El id del subprograma no puede ser nulo.");
        SubPrograma subPrograma = daoSubPrograma.findById(id)
                .orElseThrow(() -> new DataValidationException("No se encontró un subprograma con el id especificado."));
        String username = obtenerNombreUsuario(accessToken);

        subPrograma.setEstado(Estado.I);
        subPrograma.setFechaModificacion(new Date());
        subPrograma.setUsuarioModificacion(username);
        daoSubPrograma.save(subPrograma);

        // Desvincular proyectos asociados
        if (subPrograma.getProyectos() != null) {
            for (Proyecto proyecto : subPrograma.getProyectos()) {
                serviceProyecto.delete(proyecto.getId(), accessToken);
            }
        }
    }

    private void validateDtoSubPrograma(DtoSubPrograma dtoSubPrograma) {
        if (dtoSubPrograma == null) {
            throw new DataValidationException("DtoSubPrograma no puede ser nulo.");
        }
        if (dtoSubPrograma.getCodigo() == null || dtoSubPrograma.getCodigo().isEmpty()) {
            throw new DataValidationException("El código del subprograma no puede ser nulo o vacío.");
        }
        if (dtoSubPrograma.getDescripcion() == null || dtoSubPrograma.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción es requerida.");
        }
        if (dtoSubPrograma.getNombre() == null || dtoSubPrograma.getNombre().isEmpty()) {
            throw new DataValidationException("El nombre es requerido.");
        }
        if (dtoSubPrograma.getProgramaId() == null) {
            throw new DataValidationException("El id del programa es requerido.");
        }
    }

    private String obtenerNombreUsuario(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private SubPrograma crearSubPrograma(DtoSubPrograma dto, String username) {
        SubPrograma subPrograma = new SubPrograma();
        subPrograma.setCodigo(dto.getCodigo());
        subPrograma.setFechaCreacion(new Date());
        subPrograma.setUsuarioCreacion(username);
        subPrograma.setAlineacion(TipoAlineacion.OPERATIVA);
        subPrograma.setDescripcion(dto.getDescripcion());
        subPrograma.setNombre(dto.getNombre());
        return subPrograma;
    }

    private void actualizarDatosBasicos(SubPrograma subPrograma,
                                        DtoSubPrograma dto,
                                        String username) {
        subPrograma.setFechaModificacion(new Date());
        subPrograma.setUsuarioModificacion(username);
        subPrograma.setEstado(Estado.A);
        subPrograma.setAlineacion(TipoAlineacion.OPERATIVA);
        subPrograma.setDescripcion(dto.getDescripcion());
        subPrograma.setNombre(dto.getNombre());
    }

    private void vincularProyectos(SubPrograma subPrograma, List<DtoProyecto> dtosProyecto, String username, Programa programa) {
        List<Proyecto> proyectos = obtenerProyectosExistentes(dtosProyecto, subPrograma, username, programa);
        subPrograma.setProyectos(proyectos);
    }

    private List<Proyecto> obtenerProyectosExistentes(List<DtoProyecto> dtosProyecto, SubPrograma subPrograma, String username, Programa programa) {
        if (dtosProyecto == null) return List.of();
        return dtosProyecto.stream()
                .map(dto -> {
                    Optional<Proyecto> proyectoOpt = daoProyecto.findByCodigoAndSubprograma_IdAndPrograma_Id(
                            dto.getCodigo(),
                            subPrograma.getId(),
                            programa != null ? programa.getId() : null
                    );
                    Proyecto proyecto = proyectoOpt.orElseGet(() -> {
                        Proyecto nuevo = new Proyecto();
                        nuevo.setCodigo(dto.getCodigo());
                        nuevo.setNombre(dto.getNombre());
                        nuevo.setDescripcion(dto.getDescripcion());
                        nuevo.setEstado(dto.getEstado());
                        nuevo.setAlineacion(dto.getAlineacion());
                        nuevo.setUsuarioCreacion(username);
                        nuevo.setFechaCreacion(new Date());
                        nuevo.setSubprograma(subPrograma);
                        nuevo.setPrograma(programa);
                        return nuevo;
                    });
                    proyecto.setSubprograma(subPrograma);
                    proyecto.setPrograma(programa);
                    proyecto.setNombre(dto.getNombre());
                    proyecto.setDescripcion(dto.getDescripcion());
                    proyecto.setEstado(dto.getEstado());
                    proyecto.setAlineacion(dto.getAlineacion());
                    proyecto.setUsuarioModificacion(username);
                    proyecto.setFechaModificacion(new Date());
                    return daoProyecto.save(proyecto);
                })
                .collect(Collectors.toList());
    }


    private void desvincularProyecto(Proyecto proyecto, String username) {
        proyecto.setSubprograma(null);
        proyecto.setPrograma(null);
        proyecto.setFechaModificacion(new Date());
        proyecto.setUsuarioModificacion(username);
        daoProyecto.save(proyecto);
    }

    private DtoSubPrograma convertToDto(SubPrograma subPrograma) {
        if (subPrograma == null) {
            return null;
        }
        DtoSubPrograma dto = new DtoSubPrograma();
        dto.setId(subPrograma.getId()); // ID del subprograma
        dto.setCodigo(subPrograma.getCodigo());
        dto.setFechaCreacion(subPrograma.getFechaCreacion());
        dto.setUsuarioCreacion(subPrograma.getUsuarioCreacion());
        dto.setFechaModificacion(subPrograma.getFechaModificacion());
        dto.setUsuarioModificacion(subPrograma.getUsuarioModificacion());
        dto.setEstado(subPrograma.getEstado());
        dto.setAlineacion(subPrograma.getAlineacion());
        dto.setDescripcion(subPrograma.getDescripcion());
        dto.setNombre(subPrograma.getNombre());

        if (subPrograma.getPrograma() != null) {
            dto.setProgramaId(subPrograma.getPrograma().getId()); // ID del programa padre
        }

        if (subPrograma.getProyectos() != null) {
            List<DtoProyecto> proyectos = subPrograma.getProyectos().stream()
                    .map(p -> {
                        DtoProyecto dtoProyecto = new DtoProyecto();
                        dtoProyecto.setId(p.getId()); // ID del proyecto
                        dtoProyecto.setCodigo(p.getCodigo());
                        dtoProyecto.setNombre(p.getNombre());
                        dtoProyecto.setDescripcion(p.getDescripcion());
                        dtoProyecto.setEstado(p.getEstado());
                        dtoProyecto.setAlineacion(p.getAlineacion());
                        dtoProyecto.setSubProgramaId(subPrograma.getId());
                        dtoProyecto.setProgramaId(subPrograma.getPrograma() != null ? subPrograma.getPrograma().getId() : null);
                        return dtoProyecto;
                    })
                    .collect(Collectors.toList());
            dto.setProyectos(proyectos);
        }
        return dto;
    }
}