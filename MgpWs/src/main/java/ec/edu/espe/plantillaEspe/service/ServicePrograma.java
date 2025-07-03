package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoPrograma;
import ec.edu.espe.plantillaEspe.dao.DaoSubPrograma;
import ec.edu.espe.plantillaEspe.dto.DtoPrograma;
import ec.edu.espe.plantillaEspe.dto.DtoSubPrograma;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Programa;
import ec.edu.espe.plantillaEspe.model.SubPrograma;
import ec.edu.espe.plantillaEspe.service.IService.IServicePrograma;
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
public class ServicePrograma implements IServicePrograma {

    private final DaoPrograma daoPrograma;
    private final UserInfoService userInfoService;
    private final DaoSubPrograma daoSubPrograma;
    private final ServiceSubPrograma serviceSubPrograma;

    @Autowired
    public ServicePrograma(DaoPrograma daoPrograma,
                          UserInfoService userInfoService,
                          DaoSubPrograma daoSubPrograma,
                          ServiceSubPrograma serviceSubPrograma) {
        this.daoPrograma = daoPrograma;
        this.userInfoService = userInfoService;
        this.daoSubPrograma = daoSubPrograma;
        this.serviceSubPrograma = serviceSubPrograma;
    }

    @Override
    public DtoPrograma find(Long id) {
        if (id == null) throw new DataValidationException("El id del programa no puede ser nulo.");
        return daoPrograma.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un programa con el id especificado."));
    }

    @Override
    public DtoPrograma findByCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) throw new DataValidationException("El código no puede ser nulo o vacío.");
        return daoPrograma.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un programa con el código especificado."));
    }

    @Override
    public List<DtoPrograma> findAll() {
        return daoPrograma.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<DtoPrograma> findAllActivos() {
        return daoPrograma.findByEstado(Estado.A).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Page<DtoPrograma> findAll(Pageable pageable) {
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");
        return daoPrograma.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public Page<DtoPrograma> findAllActivos(Pageable pageable) {
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");
        return daoPrograma.findByEstado(Estado.A, pageable).map(this::convertToDto);
    }

    @Override
    public Page<DtoPrograma> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoPrograma.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } else {
            List<DtoPrograma> programas = daoPrograma.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(programas, searchCriteria, pageable);
        }
    }

    @Override
    @Transactional
    public DtoPrograma save(DtoPrograma dtoPrograma, String accessToken) {
        validateDtoPrograma(dtoPrograma);

        Optional<Programa> existente = daoPrograma.findByCodigo(dtoPrograma.getCodigo());
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        if (existente.isPresent()) {
            Programa programa = existente.get();
            if (Estado.A.equals(programa.getEstado())) {
                throw new DataValidationException("Ya existe un programa activo con el código especificado.");
            }
            programa.setEstado(Estado.A);
            programa.setDescripcion(dtoPrograma.getDescripcion());
            programa.setNombre(dtoPrograma.getNombre());
            programa.setFechaCreacion(new Date());
            programa.setUsuarioCreacion(username);
            programa.setAlineacion(TipoAlineacion.OPERATIVA);
            vincularSubProgramas(programa, dtoPrograma.getSubProgramaList(), username);
            return convertToDto(daoPrograma.save(programa));
        }

        Programa programa = new Programa();
        programa.setCodigo(dtoPrograma.getCodigo());
        programa.setFechaCreacion(new Date());
        programa.setUsuarioCreacion(username);
        programa.setEstado(Estado.A);
        programa.setAlineacion(TipoAlineacion.OPERATIVA);
        programa.setDescripcion(dtoPrograma.getDescripcion());
        programa.setNombre(dtoPrograma.getNombre());
        vincularSubProgramas(programa, dtoPrograma.getSubProgramaList(), username);

        return convertToDto(daoPrograma.save(programa));
    }

    @Override
    @Transactional
    public DtoPrograma update(DtoPrograma dtoPrograma, String accessToken) {
        validateDtoPrograma(dtoPrograma);

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        Programa programa = daoPrograma.findById(dtoPrograma.getId())
                .orElseThrow(() -> new DataValidationException("El programa con el id especificado no existe."));

        if (Estado.I.equals(dtoPrograma.getEstado()) && Estado.A.equals(programa.getEstado())) {
            throw new DataValidationException("Para desactivar un programa use el método delete()");
        }

        programa.setFechaModificacion(new Date());
        programa.setUsuarioModificacion(username);
        programa.setEstado(Estado.A);
        programa.setAlineacion(TipoAlineacion.OPERATIVA);
        programa.setDescripcion(dtoPrograma.getDescripcion());
        programa.setNombre(dtoPrograma.getNombre());
        actualizarSubProgramasAsociados(programa, dtoPrograma.getSubProgramaList(), username);

        return convertToDto(daoPrograma.save(programa));
    }

    @Override
    @Transactional
    public void delete(Long id, String accessToken) {
        if (id == null) throw new DataValidationException("El id del programa no puede ser nulo.");
        Programa programa = daoPrograma.findById(id)
                .orElseThrow(() -> new DataValidationException("No se encontró un programa con el id especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        programa.setUsuarioModificacion(username);
        programa.setEstado(Estado.I);
        programa.setFechaModificacion(new Date());
        daoPrograma.save(programa);

        // Desvincular subprogramas asociados
        if (programa.getSubProgramas() != null) {
            for (SubPrograma subPrograma : programa.getSubProgramas()) {
                serviceSubPrograma.delete(subPrograma.getId(), accessToken);
            }
        }
    }

    private void validateDtoPrograma(DtoPrograma dtoPrograma) {
        if (dtoPrograma == null) {
            throw new DataValidationException("DtoPrograma no puede ser nulo.");
        }
        if (dtoPrograma.getCodigo() == null || dtoPrograma.getCodigo().isEmpty()) {
            throw new DataValidationException("El código del programa no puede ser nulo o vacío.");
        }
        if (dtoPrograma.getDescripcion() == null || dtoPrograma.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoPrograma.getNombre() == null || dtoPrograma.getNombre().isEmpty()) {
            throw new DataValidationException("Nombre es requerido.");
        }
    }

    private void vincularSubProgramas(Programa programa, List<DtoSubPrograma> dtosSubPrograma, String username) {
        List<SubPrograma> subProgramas = obtenerSubProgramasExistentes(dtosSubPrograma, programa);
        actualizarReferenciasSubProgramas(subProgramas, programa, username);
        programa.setSubProgramas(subProgramas);
    }

    private List<SubPrograma> obtenerSubProgramasExistentes(List<DtoSubPrograma> dtosSubPrograma, Programa programa) {
        if (dtosSubPrograma == null) return List.of();
        return dtosSubPrograma.stream()
                .map(dto -> {
                    if (dto.getId() != null) {
                        return daoSubPrograma.findById(dto.getId())
                                .orElseThrow(() -> new DataValidationException(
                                        "No se encontró un subprograma con el id especificado: " + dto.getId()));
                    } else {
                        SubPrograma nuevo = new SubPrograma();
                        nuevo.setCodigo(dto.getCodigo());
                        nuevo.setDescripcion(dto.getDescripcion());
                        nuevo.setNombre(dto.getNombre());
                        nuevo.setEstado(dto.getEstado() != null ? dto.getEstado() : Estado.A);
                        nuevo.setAlineacion(dto.getAlineacion() != null ? dto.getAlineacion() : TipoAlineacion.OPERATIVA);
                        nuevo.setPrograma(programa);
                        nuevo.setFechaCreacion(new Date());
                        nuevo.setUsuarioCreacion("system");
                        return daoSubPrograma.save(nuevo);
                    }
                })
                .collect(Collectors.toList());
    }

    private void actualizarSubProgramasAsociados(Programa programa,
                                                 List<DtoSubPrograma> nuevosSubProgramas,
                                                 String username) {
        List<SubPrograma> subProgramasActualizados = obtenerSubProgramasExistentes(nuevosSubProgramas, programa);
        actualizarReferenciasSubProgramas(subProgramasActualizados, programa, username);
        desvincularSubProgramasEliminados(programa.getSubProgramas(), subProgramasActualizados, username);
        programa.setSubProgramas(subProgramasActualizados);
    }

    private void actualizarReferenciasSubProgramas(List<SubPrograma> subProgramas,
                                                   Programa programa,
                                                   String username) {
        for (SubPrograma subPrograma : subProgramas) {
            subPrograma.setPrograma(programa);
            subPrograma.setFechaModificacion(new Date());
            subPrograma.setEstado(Estado.A);
            subPrograma.setUsuarioModificacion(username);
            daoSubPrograma.save(subPrograma);
        }
    }

    private void desvincularSubProgramasEliminados(List<SubPrograma> subProgramasActuales,
                                                   List<SubPrograma> subProgramasNuevos,
                                                   String username) {
        if (subProgramasActuales == null) return;
        for (SubPrograma subProgramaActual : subProgramasActuales) {
            if (subProgramasNuevos.stream().noneMatch(sp -> sp.getId().equals(subProgramaActual.getId()))) {
                subProgramaActual.setPrograma(null);
                subProgramaActual.setFechaModificacion(new Date());
                subProgramaActual.setUsuarioModificacion(username);
                daoSubPrograma.save(subProgramaActual);
            }
        }
    }

    private DtoPrograma convertToDto(Programa programa) {
        if (programa == null) {
            return null;
        }
        DtoPrograma dto = new DtoPrograma();
        dto.setId(programa.getId());
        dto.setCodigo(programa.getCodigo());
        dto.setFechaCreacion(programa.getFechaCreacion());
        dto.setUsuarioCreacion(programa.getUsuarioCreacion());
        dto.setFechaModificacion(programa.getFechaModificacion());
        dto.setUsuarioModificacion(programa.getUsuarioModificacion());
        dto.setEstado(programa.getEstado());
        dto.setAlineacion(programa.getAlineacion());
        dto.setDescripcion(programa.getDescripcion());
        dto.setNombre(programa.getNombre());

        if (programa.getSubProgramas() != null) {
            List<DtoSubPrograma> subProgramas = programa.getSubProgramas().stream()
                    .map(sp -> {
                        DtoSubPrograma dtoSubPrograma = new DtoSubPrograma();
                        dtoSubPrograma.setId(sp.getId());
                        dtoSubPrograma.setCodigo(sp.getCodigo());
                        dtoSubPrograma.setFechaCreacion(sp.getFechaCreacion());
                        dtoSubPrograma.setUsuarioCreacion(sp.getUsuarioCreacion());
                        dtoSubPrograma.setFechaModificacion(sp.getFechaModificacion());
                        dtoSubPrograma.setUsuarioModificacion(sp.getUsuarioModificacion());
                        dtoSubPrograma.setEstado(sp.getEstado());
                        dtoSubPrograma.setDescripcion(sp.getDescripcion());
                        dtoSubPrograma.setNombre(sp.getNombre());
                        dtoSubPrograma.setAlineacion(sp.getAlineacion());
                        dtoSubPrograma.setProgramaId(programa.getId());
                        return dtoSubPrograma;
                    })
                    .collect(Collectors.toList());
            dto.setSubProgramaList(subProgramas);
        }
        return dto;
    }
}