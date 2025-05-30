package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoTipoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoTipoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoProyectoSeccion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.TipoProyecto;
import ec.edu.espe.plantillaEspe.service.IService.IServiceTipoProyecto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceTipoProyecto implements IServiceTipoProyecto {

    private final DaoTipoProyecto daoTipoProyecto;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceTipoProyecto(DaoTipoProyecto daoTipoProyecto, UserInfoService userInfoService) {
        this.daoTipoProyecto = daoTipoProyecto;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoTipoProyecto find(String codigo) {
        validateCodigo(codigo);
        return daoTipoProyecto.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un proyecto con el código especificado."));
    }

    @Override
    public List<DtoTipoProyecto> findAll() {
        try {
            return daoTipoProyecto.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los proyectos.", e);
        }
    }

    @Override
    public Page<DtoTipoProyecto> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoTipoProyecto.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los proyectos paginados.", e);
        }
    }

    @Override
    @Transactional
    public DtoTipoProyecto save(DtoTipoProyecto dtoTipoProyecto, String accessToken) {
        validateDtoProyecto(dtoTipoProyecto);

        if (daoTipoProyecto.findByCodigo(dtoTipoProyecto.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe un proyecto con el código especificado.");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        TipoProyecto tipoProyecto = new TipoProyecto();
        tipoProyecto.setCodigo(dtoTipoProyecto.getCodigo());
        tipoProyecto.setFechaCreacion(new Date());
        tipoProyecto.setUsuarioCreacion(username);

        return convertToDto(daoTipoProyecto.save(tipoProyecto));
    }

    @Override
    @Transactional
    public DtoTipoProyecto update(DtoTipoProyecto dtoTipoProyecto, String accessToken) {
        validateDtoProyecto(dtoTipoProyecto);

        TipoProyecto tipoProyectoActual = daoTipoProyecto.findByCodigo(dtoTipoProyecto.getCodigo())
                .orElseThrow(() -> new DataValidationException("El proyecto con el código especificado no existe."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        tipoProyectoActual.setFechaModificacion(new Date());
        tipoProyectoActual.setUsuarioModificacion(username);

        return convertToDto(daoTipoProyecto.save(tipoProyectoActual));
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        validateCodigo(codigo);
        TipoProyecto tipoProyecto = daoTipoProyecto.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un proyecto con el código especificado."));

        try {
            daoTipoProyecto.delete(tipoProyecto);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el proyecto.", e);
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del proyecto no puede ser nulo o vacío.");
        }
    }

    private void validateDtoProyecto(DtoTipoProyecto dtoTipoProyecto) {
        if (dtoTipoProyecto == null) {
            throw new DataValidationException("DtoProyecto no puede ser nulo.");
        }
        validateCodigo(dtoTipoProyecto.getCodigo());
    }

    private DtoTipoProyecto convertToDto(TipoProyecto tipoProyecto) {
        if (tipoProyecto == null) {
            return null;
        }
        DtoTipoProyecto dto = new DtoTipoProyecto();
        dto.setId(tipoProyecto.getId());
        dto.setCodigo(tipoProyecto.getCodigo());
        dto.setFechaCreacion(tipoProyecto.getFechaCreacion());
        dto.setUsuarioCreacion(tipoProyecto.getUsuarioCreacion());
        dto.setFechaModificacion(tipoProyecto.getFechaModificacion());
        dto.setUsuarioModificacion(tipoProyecto.getUsuarioModificacion());

        if (tipoProyecto.getProyectoSecciones() != null) {
            List<DtoProyectoSeccion> secciones = tipoProyecto.getProyectoSecciones().stream()
                    .map(ps -> {
                        DtoProyectoSeccion dtoProyectoSeccion = new DtoProyectoSeccion();
                        dtoProyectoSeccion.setId(ps.getId());
                        dtoProyectoSeccion.setCodigo(ps.getCodigo());
                        dtoProyectoSeccion.setCodigoProyectoFK(ps.getTipoProyecto().getCodigo());
                        dtoProyectoSeccion.setCodigoSeccionFk(ps.getSeccion().getCodigo());
                        dtoProyectoSeccion.setFechaCreacion(ps.getFechaCreacion());
                        dtoProyectoSeccion.setUsuarioCreacion(ps.getUsuarioCreacion());
                        dtoProyectoSeccion.setFechaModificacion(ps.getFechaModificacion());
                        dtoProyectoSeccion.setUsuarioModificacion(ps.getUsuarioModificacion());
                        return dtoProyectoSeccion;
                    })
                    .collect(Collectors.toList()); // Cambiado a toList()
            dto.setSecciones(secciones);
        }
        return dto;
    }
}