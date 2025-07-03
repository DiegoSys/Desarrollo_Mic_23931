package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoTipoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoTipoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoProyectoSeccion;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.TipoProyecto;
import ec.edu.espe.plantillaEspe.model.ProyectoSeccion;
import ec.edu.espe.plantillaEspe.service.IService.IServiceTipoProyecto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
                .orElseThrow(() -> new DataValidationException("No se encontró un tipo de proyecto con el código especificado."));
    }

    @Override
    public List<DtoTipoProyecto> findAll() {
        try {
            return daoTipoProyecto.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los tipos de proyecto.", e);
        }
    }

    @Override
    public List<DtoTipoProyecto> findAllActivos() {
        try {
            return daoTipoProyecto.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los tipos de proyecto activos.", e);
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
            throw new RuntimeException("Error al obtener los tipos de proyecto paginados.", e);
        }
    }

    @Override
    public Page<DtoTipoProyecto> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoTipoProyecto.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } else {
            List<DtoTipoProyecto> activos = daoTipoProyecto.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(activos, searchCriteria, pageable);
        }
    }

    @Override
    @Transactional
    public DtoTipoProyecto save(DtoTipoProyecto dtoTipoProyecto, String accessToken) {
        validateDtoTipoProyecto(dtoTipoProyecto);

        Optional<TipoProyecto> existente = daoTipoProyecto.findByCodigo(dtoTipoProyecto.getCodigo());
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        if (existente.isPresent()) {
            TipoProyecto tipoProyecto = existente.get();
            if (Estado.A.equals(tipoProyecto.getEstado())) {
                throw new DataValidationException("Ya existe un tipo de proyecto activo con el código especificado.");
            }
            // Reactivar registro inactivo
            tipoProyecto.setEstado(Estado.A);
            tipoProyecto.setNombre(dtoTipoProyecto.getNombre());
            tipoProyecto.setFechaCreacion(new Date());
            tipoProyecto.setUsuarioCreacion(username);
            return convertToDto(daoTipoProyecto.save(tipoProyecto));
        }

        TipoProyecto tipoProyecto = new TipoProyecto();
        tipoProyecto.setCodigo(dtoTipoProyecto.getCodigo());
        tipoProyecto.setNombre(dtoTipoProyecto.getNombre());
        tipoProyecto.setEstado(Estado.A);
        tipoProyecto.setFechaCreacion(new Date());
        tipoProyecto.setUsuarioCreacion(username);

        return convertToDto(daoTipoProyecto.save(tipoProyecto));
    }

    @Override
    @Transactional
    public DtoTipoProyecto update(DtoTipoProyecto dtoTipoProyecto, String accessToken) {
        validateDtoTipoProyecto(dtoTipoProyecto);

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        TipoProyecto tipoProyecto = daoTipoProyecto.findByCodigo(dtoTipoProyecto.getCodigo())
                .orElseThrow(() -> new DataValidationException("El tipo de proyecto con el código especificado no existe."));

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoTipoProyecto.getEstado()) && Estado.A.equals(tipoProyecto.getEstado())) {
            throw new DataValidationException("Para desactivar un tipo de proyecto use el método delete()");
        }

        tipoProyecto.setFechaModificacion(new Date());
        tipoProyecto.setUsuarioModificacion(username);
        tipoProyecto.setEstado(Estado.A);
        tipoProyecto.setNombre(dtoTipoProyecto.getNombre());

        return convertToDto(daoTipoProyecto.save(tipoProyecto));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        TipoProyecto tipoProyecto = daoTipoProyecto.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un tipo de proyecto con el código especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        tipoProyecto.setUsuarioModificacion(username);
        tipoProyecto.setEstado(Estado.I);
        tipoProyecto.setFechaModificacion(new Date());
        daoTipoProyecto.save(tipoProyecto);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del tipo de proyecto no puede ser nulo o vacío.");
        }
    }

    private void validateDtoTipoProyecto(DtoTipoProyecto dtoTipoProyecto) {
        if (dtoTipoProyecto == null) {
            throw new DataValidationException("DtoTipoProyecto no puede ser nulo.");
        }
        validateCodigo(dtoTipoProyecto.getCodigo());
        if (dtoTipoProyecto.getNombre() == null || dtoTipoProyecto.getNombre().isEmpty()) {
            throw new DataValidationException("Nombre es requerido.");
        }
        if (dtoTipoProyecto.getDescripcion() == null || dtoTipoProyecto.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
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
        dto.setEstado(tipoProyecto.getEstado());
        dto.setNombre(tipoProyecto.getNombre());

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
                    .collect(Collectors.toList());
            dto.setSecciones(secciones);
        }
        return dto;
    }
}