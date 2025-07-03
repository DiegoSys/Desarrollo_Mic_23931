package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoProyectoSeccion;
import ec.edu.espe.plantillaEspe.dao.DaoTipoProyecto;
import ec.edu.espe.plantillaEspe.dao.DaoSeccion;
import ec.edu.espe.plantillaEspe.dto.DtoProyectoSeccion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.ProyectoSeccion;
import ec.edu.espe.plantillaEspe.model.TipoProyecto;
import ec.edu.espe.plantillaEspe.model.Seccion;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProyectoSeccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ServiceProyectoSeccion implements IServiceProyectoSeccion {

    private final DaoProyectoSeccion daoProyectoSeccion;
    private final DaoTipoProyecto daoTipoProyecto;
    private final DaoSeccion daoSeccion;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceProyectoSeccion(
            DaoProyectoSeccion daoProyectoSeccion,
            DaoTipoProyecto daoTipoProyecto,
            DaoSeccion daoSeccion,
            UserInfoService userInfoService) {
        this.daoProyectoSeccion = daoProyectoSeccion;
        this.daoTipoProyecto = daoTipoProyecto;
        this.daoSeccion = daoSeccion;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoProyectoSeccion find(String codigo) {
        validateCodigo(codigo);
        return daoProyectoSeccion.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró la relación proyecto-sección con el código especificado."));
    }

    @Override
    public List<DtoProyectoSeccion> findAll() {
        try {
            return daoProyectoSeccion.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los proyecto-sección.", e);
        }
    }

    @Override
    public Page<DtoProyectoSeccion> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoProyectoSeccion.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las relaciones proyecto-sección paginadas.", e);
        }
    }

    @Override
    public Page<DtoProyectoSeccion> findByCodigoProyecto(String codigoProyecto, Pageable pageable) {
        validateCodigo(codigoProyecto);
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoProyectoSeccion.findByTipoProyectoCodigo(codigoProyecto, pageable)
                    .map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las relaciones por código de proyecto paginadas.", e);
        }
    }

    @Override
    @Transactional
    public DtoProyectoSeccion save(DtoProyectoSeccion dtoProyectoSeccion, String accessToken) {
        System.out.println(dtoProyectoSeccion.getCodigoProyectoFK() + " " + dtoProyectoSeccion.getCodigoSeccionFk());
        validateDto(dtoProyectoSeccion);

        if (daoProyectoSeccion.findByCodigo(dtoProyectoSeccion.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe una relación proyecto-sección con el código especificado.");
        }

        TipoProyecto tipoProyecto = daoTipoProyecto.findByCodigo(dtoProyectoSeccion.getCodigoProyectoFK())
                .orElseThrow(() -> new DataValidationException("No existe un proyecto con el código: "
                        + dtoProyectoSeccion.getCodigoProyectoFK()));
        System.out.println(tipoProyecto);

        Seccion seccion = daoSeccion.findByCodigo(dtoProyectoSeccion.getCodigoSeccionFk())
                .orElseThrow(() -> new DataValidationException("No existe una sección con el código: "
                        + dtoProyectoSeccion.getCodigoSeccionFk()));
        System.out.println(seccion);

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        ProyectoSeccion proyectoSeccion = new ProyectoSeccion();
        proyectoSeccion.setCodigo(dtoProyectoSeccion.getCodigo());
        proyectoSeccion.setTipoProyecto(tipoProyecto);
        proyectoSeccion.setSeccion(seccion);
        proyectoSeccion.setOrden(dtoProyectoSeccion.getOrden());
        proyectoSeccion.setFechaCreacion(new Date());
        proyectoSeccion.setUsuarioCreacion(username);

        return convertToDto(daoProyectoSeccion.save(proyectoSeccion));
    }

    @Override
    @Transactional
    public DtoProyectoSeccion update(DtoProyectoSeccion dtoProyectoSeccion, String accessToken) {
        validateDto(dtoProyectoSeccion);

        ProyectoSeccion proyectoSeccionActual = daoProyectoSeccion.findByCodigo(dtoProyectoSeccion.getCodigo())
                .orElseThrow(() -> new DataValidationException("La relación proyecto-sección con el código especificado no existe."));

        if (dtoProyectoSeccion.getCodigoProyectoFK() != null) {
            TipoProyecto tipoProyecto = daoTipoProyecto.findByCodigo(dtoProyectoSeccion.getCodigoProyectoFK())
                    .orElseThrow(() -> new DataValidationException("No existe un proyecto con el código: "
                            + dtoProyectoSeccion.getCodigoProyectoFK()));
            proyectoSeccionActual.setTipoProyecto(tipoProyecto);
        }

        if (dtoProyectoSeccion.getCodigoSeccionFk() != null) {
            Seccion seccion = daoSeccion.findByCodigo(dtoProyectoSeccion.getCodigoSeccionFk())
                    .orElseThrow(() -> new DataValidationException("No existe una sección con el código: "
                            + dtoProyectoSeccion.getCodigoSeccionFk()));
            proyectoSeccionActual.setSeccion(seccion);
        }

        if (dtoProyectoSeccion.getOrden() != null) {
            proyectoSeccionActual.setOrden(dtoProyectoSeccion.getOrden());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        proyectoSeccionActual.setFechaModificacion(new Date());
        proyectoSeccionActual.setUsuarioModificacion(username);

        return convertToDto(daoProyectoSeccion.save(proyectoSeccionActual));
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        validateCodigo(codigo);
        ProyectoSeccion proyectoSeccion = daoProyectoSeccion.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró la relación proyecto-sección con el código especificado."));

        try {
            daoProyectoSeccion.delete(proyectoSeccion);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la relación proyecto-sección.", e);
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código no puede ser nulo o vacío.");
        }
    }

    private void validateDto(DtoProyectoSeccion dto) {
        if (dto == null) {
            throw new DataValidationException("El DTO no puede ser nulo");
        }
        validateCodigo(dto.getCodigo());

        if (dto.getCodigoProyectoFK() == null || dto.getCodigoProyectoFK().isEmpty()) {
            throw new DataValidationException("El código del proyecto es requerido");
        }

        if (dto.getCodigoSeccionFk() == null || dto.getCodigoSeccionFk().isEmpty()) {
            throw new DataValidationException("El código de la sección es requerido");
        }

        if (dto.getOrden() == null || dto.getOrden() <= 0) {
            throw new DataValidationException("El orden es requerido y debe ser mayor a cero");
        }
    }

    private DtoProyectoSeccion convertToDto(ProyectoSeccion proyectoSeccion) {
        if (proyectoSeccion == null) {
            return null;
        }
        DtoProyectoSeccion dto = new DtoProyectoSeccion();
        dto.setId(proyectoSeccion.getId());
        dto.setCodigo(proyectoSeccion.getCodigo());
        dto.setCodigoProyectoFK(proyectoSeccion.getTipoProyecto().getCodigo());
        dto.setCodigoSeccionFk(proyectoSeccion.getSeccion().getCodigo());
        dto.setOrden(proyectoSeccion.getOrden());
        dto.setFechaCreacion(proyectoSeccion.getFechaCreacion());
        dto.setUsuarioCreacion(proyectoSeccion.getUsuarioCreacion());
        dto.setFechaModificacion(proyectoSeccion.getFechaModificacion());
        dto.setUsuarioModificacion(proyectoSeccion.getUsuarioModificacion());
        return dto;
    }
}