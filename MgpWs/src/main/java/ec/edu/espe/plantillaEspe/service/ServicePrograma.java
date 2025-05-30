package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoPrograma;
import ec.edu.espe.plantillaEspe.dao.DaoSubPrograma;
import ec.edu.espe.plantillaEspe.dto.DtoPrograma;
import ec.edu.espe.plantillaEspe.dto.DtoSubPrograma;
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
import java.util.stream.Collectors;

@Service
public class ServicePrograma implements IServicePrograma {

    private final DaoPrograma daoPrograma;
    private final UserInfoService userInfoService;
    private final DaoSubPrograma daoSubPrograma;

    @Autowired
    public ServicePrograma(DaoPrograma daoPrograma,
                          UserInfoService userInfoService,
                          DaoSubPrograma daoSubPrograma) {
        this.daoSubPrograma = daoSubPrograma;
        this.daoPrograma = daoPrograma;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoPrograma find(String codigo) {
        validateCodigo(codigo);
        return daoPrograma.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un programa con el código especificado."));
    }

    @Override
    public List<DtoPrograma> findAll() {
        try {
            return daoPrograma.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los programas.", e);
        }
    }

    @Override
    public Page<DtoPrograma> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPrograma.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los programas paginados.", e);
        }
    }

    @Override
    @Transactional
    public DtoPrograma save(DtoPrograma dtoPrograma, String accessToken) {
        validarNuevoPrograma(dtoPrograma);
        String username = obtenerNombreUsuario(accessToken);

        Programa programa = crearPrograma(dtoPrograma, username);
        vincularSubProgramas(programa, dtoPrograma.getSubProgramaList(), username);

        return convertToDto(daoPrograma.save(programa));
    }

    @Override
    @Transactional
    public DtoPrograma update(DtoPrograma dtoPrograma, String accessToken) {
        if (dtoPrograma.getCodigo() == null || dtoPrograma.getCodigo().isEmpty()) {
            throw new DataValidationException("El código del programa no puede ser nulo o vacío.");
        }

        validateDtoPrograma(dtoPrograma);
        String username = obtenerNombreUsuario(accessToken);

        Programa programa = obtenerProgramaExistente(dtoPrograma.getCodigo());
        actualizarDatosBasicos(programa, dtoPrograma, username);
        actualizarSubProgramasAsociados(programa, dtoPrograma.getSubProgramaList(), username);

        return convertToDto(daoPrograma.save(programa));
    }

    private void validarNuevoPrograma(DtoPrograma dtoPrograma) {
        validateDtoPrograma(dtoPrograma);
        if (daoPrograma.findByCodigo(dtoPrograma.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe un programa con el código especificado.");
        }
    }

    private String obtenerNombreUsuario(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private Programa crearPrograma(DtoPrograma dto, String username) {
        Programa programa = new Programa();
        programa.setCodigo(dto.getCodigo());
        programa.setFechaCreacion(new Date());
        programa.setUsuarioCreacion(username);
        programa.setEstado(dto.getEstado());
        programa.setDescripcion(dto.getDescripcion());
        programa.setNombre(dto.getNombre());
        return programa;
    }

    private void vincularSubProgramas(Programa programa, List<DtoSubPrograma> dtosSubPrograma, String username) {
        List<SubPrograma> subProgramas = obtenerSubProgramasExistentes(dtosSubPrograma);
        actualizarReferenciasSubProgramas(subProgramas, programa, username);
        programa.setSubProgramas(subProgramas);
    }

    private List<SubPrograma> obtenerSubProgramasExistentes(List<DtoSubPrograma> dtosSubPrograma) {
        return dtosSubPrograma.stream()
                .map(dto -> daoSubPrograma.findByCodigo(dto.getCodigo())
                        .orElseThrow(() -> new DataValidationException(
                                "No se encontró un subprograma con el código especificado.")))
                .collect(Collectors.toList());
    }

    private Programa obtenerProgramaExistente(String codigo) {
        return daoPrograma.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException(
                        "El programa con el código especificado no existe."));
    }

    private void actualizarDatosBasicos(Programa programa,
                                      DtoPrograma dto,
                                      String username) {
        programa.setFechaModificacion(new Date());
        programa.setUsuarioModificacion(username);
        programa.setEstado(dto.getEstado());
        programa.setDescripcion(dto.getDescripcion());
        programa.setNombre(dto.getNombre());
    }

    private void actualizarSubProgramasAsociados(Programa programa,
                                               List<DtoSubPrograma> nuevosSubProgramas,
                                               String username) {
        List<SubPrograma> subProgramasActualizados = obtenerSubProgramasExistentes(nuevosSubProgramas);
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
            subPrograma.setUsuarioModificacion(username);
            daoSubPrograma.save(subPrograma);
        }
    }

    private void desvincularSubProgramasEliminados(List<SubPrograma> subProgramasActuales,
                                                 List<SubPrograma> subProgramasNuevos,
                                                 String username) {
        for (SubPrograma subProgramaActual : subProgramasActuales) {
            if (subProgramaDebeSerDesvinculado(subProgramaActual, subProgramasNuevos)) {
                desvincularSubPrograma(subProgramaActual, username);
            }
        }
    }

    private boolean subProgramaDebeSerDesvinculado(SubPrograma subProgramaActual, List<SubPrograma> subProgramasNuevos) {
        return subProgramasNuevos.stream()
                .noneMatch(subProgramaNuevo -> subProgramaNuevo.getCodigo().equals(subProgramaActual.getCodigo()));
    }

    private void desvincularSubPrograma(SubPrograma subPrograma, String username) {
        subPrograma.setPrograma(null);
        subPrograma.setFechaModificacion(new Date());
        subPrograma.setUsuarioModificacion(username);
        daoSubPrograma.save(subPrograma);
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        validateCodigo(codigo);
        Programa programa = daoPrograma.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un programa con el código especificado."));

        try {
            daoPrograma.delete(programa);
            List<SubPrograma> subProgramaList = programa.getSubProgramas();
            for (SubPrograma subPrograma : subProgramaList) {
                subPrograma.setPrograma(null);
                subPrograma.setFechaModificacion(new Date());
                subPrograma.setUsuarioModificacion(programa.getUsuarioModificacion());
                daoSubPrograma.save(subPrograma);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el programa.", e);
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del programa no puede ser nulo o vacío.");
        }
    }

    private void validateDtoPrograma(DtoPrograma dtoPrograma) {
        if (dtoPrograma == null) {
            throw new DataValidationException("DtoPrograma no puede ser nulo.");
        }
        validateCodigo(dtoPrograma.getCodigo());
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
                        
                        return dtoSubPrograma;
                    })
                    .collect(Collectors.toList());
            dto.setSubProgramaList(subProgramas);
        }
        return dto;
    }
}
