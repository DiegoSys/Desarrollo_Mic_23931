package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoSeccionCampo;
import ec.edu.espe.plantillaEspe.dao.DaoSeccion;
import ec.edu.espe.plantillaEspe.dao.DaoCampo;
import ec.edu.espe.plantillaEspe.dto.DtoSeccionCampo;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.SeccionCampo;
import ec.edu.espe.plantillaEspe.model.Seccion;
import ec.edu.espe.plantillaEspe.model.Campo;
import ec.edu.espe.plantillaEspe.service.IService.IServiceSeccionCampo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ServiceSeccionCampo implements IServiceSeccionCampo {

    private final DaoSeccionCampo daoSeccionCampo;
    private final DaoSeccion daoSeccion;
    private final DaoCampo daoCampo;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceSeccionCampo(
            DaoSeccionCampo daoSeccionCampo,
            DaoSeccion daoSeccion,
            DaoCampo daoCampo,
            UserInfoService userInfoService) {
        this.daoSeccionCampo = daoSeccionCampo;
        this.daoSeccion = daoSeccion;
        this.daoCampo = daoCampo;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoSeccionCampo find(String codigo) {
        validateCodigo(codigo);
        return daoSeccionCampo.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró la relación sección-campo con el código especificado."));
    }

    @Override
    public List<DtoSeccionCampo> findAll() {
        try {
            return daoSeccionCampo.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los sección-campo.", e);
        }
    }

    @Override
    public Page<DtoSeccionCampo> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoSeccionCampo.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las relaciones sección-campo paginadas.", e);
        }
    }

    @Override
    public Page<DtoSeccionCampo> findByCodigoSeccion(String codigoSeccion, Pageable pageable) {
        validateCodigo(codigoSeccion);
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoSeccionCampo.findBySeccionCodigo(codigoSeccion, pageable)
                    .map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las relaciones por código de sección paginadas.", e);
        }
    }

    @Override
    public Page<DtoSeccionCampo> findByCodigoCampo(String codigoCampo, Pageable pageable) {
        validateCodigo(codigoCampo);
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoSeccionCampo.findByCampoCodigo(codigoCampo, pageable)
                    .map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las relaciones por código de campo paginadas.", e);
        }
    }

    @Override
    @Transactional
    public DtoSeccionCampo save(DtoSeccionCampo dtoSeccionCampo, String accessToken) {
        validateDto(dtoSeccionCampo);

        if (daoSeccionCampo.findByCodigo(dtoSeccionCampo.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe una relación sección-campo con el código especificado.");
        }

        Seccion seccion = daoSeccion.findByCodigo(dtoSeccionCampo.getCodigoSeccionFk())
                .orElseThrow(() -> new DataValidationException("No existe una sección con el código: "
                        + dtoSeccionCampo.getCodigoSeccionFk()));

        Campo campo = daoCampo.findByCodigo(dtoSeccionCampo.getCodigoCampoFk())
                .orElseThrow(() -> new DataValidationException("No existe un campo con el código: "
                        + dtoSeccionCampo.getCodigoCampoFk()));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        SeccionCampo seccionCampo = new SeccionCampo();
        seccionCampo.setCodigo(dtoSeccionCampo.getCodigo());
        seccionCampo.setSeccion(seccion);
        seccionCampo.setCampo(campo);
        seccionCampo.setOrden(dtoSeccionCampo.getOrden());
        seccionCampo.setFechaCreacion(new Date());
        seccionCampo.setUsuarioCreacion(username);

        return convertToDto(daoSeccionCampo.save(seccionCampo));
    }

    @Override
    @Transactional
    public DtoSeccionCampo update(DtoSeccionCampo dtoSeccionCampo, String accessToken) {
        validateDto(dtoSeccionCampo);

        SeccionCampo seccionCampoActual = daoSeccionCampo.findByCodigo(dtoSeccionCampo.getCodigo())
                .orElseThrow(() -> new DataValidationException("La relación sección-campo con el código especificado no existe."));

        if (dtoSeccionCampo.getCodigoSeccionFk() != null) {
            Seccion seccion = daoSeccion.findByCodigo(dtoSeccionCampo.getCodigoSeccionFk())
                    .orElseThrow(() -> new DataValidationException("No existe una sección con el código: "
                            + dtoSeccionCampo.getCodigoSeccionFk()));
            seccionCampoActual.setSeccion(seccion);
        }

        if (dtoSeccionCampo.getCodigoCampoFk() != null) {
            Campo campo = daoCampo.findByCodigo(dtoSeccionCampo.getCodigoCampoFk())
                    .orElseThrow(() -> new DataValidationException("No existe un campo con el código: "
                            + dtoSeccionCampo.getCodigoCampoFk()));
            seccionCampoActual.setCampo(campo);
        }

        if (dtoSeccionCampo.getOrden() != null) {
            seccionCampoActual.setOrden(dtoSeccionCampo.getOrden());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        seccionCampoActual.setFechaModificacion(new Date());
        seccionCampoActual.setUsuarioModificacion(username);

        return convertToDto(daoSeccionCampo.save(seccionCampoActual));
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        validateCodigo(codigo);
        SeccionCampo seccionCampo = daoSeccionCampo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró la relación sección-campo con el código especificado."));

        try {
            daoSeccionCampo.delete(seccionCampo);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la relación sección-campo.", e);
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código no puede ser nulo o vacío.");
        }
    }

    private void validateDto(DtoSeccionCampo dto) {
        if (dto == null) {
            throw new DataValidationException("El DTO no puede ser nulo");
        }
        validateCodigo(dto.getCodigo());

        if (dto.getCodigoSeccionFk() == null || dto.getCodigoSeccionFk().isEmpty()) {
            throw new DataValidationException("El código de la sección es requerido");
        }

        if (dto.getCodigoCampoFk() == null || dto.getCodigoCampoFk().isEmpty()) {
            throw new DataValidationException("El código del campo es requerido");
        }

        if (dto.getOrden() == null || dto.getOrden() <= 0) {
            throw new DataValidationException("El orden es requerido y debe ser mayor a cero");
        }
    }

    private DtoSeccionCampo convertToDto(SeccionCampo seccionCampo) {
        if (seccionCampo == null) {
            return null;
        }
        DtoSeccionCampo dto = new DtoSeccionCampo();
        dto.setId(seccionCampo.getId());
        dto.setCodigo(seccionCampo.getCodigo());
        dto.setCodigoSeccionFk(seccionCampo.getSeccion().getCodigo());
        dto.setCodigoCampoFk(seccionCampo.getCampo().getCodigo());
        dto.setOrden(seccionCampo.getOrden());
        dto.setFechaCreacion(seccionCampo.getFechaCreacion());
        dto.setUsuarioCreacion(seccionCampo.getUsuarioCreacion());
        dto.setFechaModificacion(seccionCampo.getFechaModificacion());
        dto.setUsuarioModificacion(seccionCampo.getUsuarioModificacion());
        return dto;
    }
}