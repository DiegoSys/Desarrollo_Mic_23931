package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoMeta;
import ec.edu.espe.plantillaEspe.dao.DaoProgNac;
import ec.edu.espe.plantillaEspe.dto.DtoProgNac;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.ProgNac;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProgNac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceProgNac implements IServiceProgNac {

    private final DaoProgNac daoProgNac;
    private final UserInfoService userInfoService;
    private final DaoMeta daoMeta;

    @Autowired
    public ServiceProgNac(DaoProgNac daoProgNac,
                          UserInfoService userInfoService,
                          DaoMeta daoMeta) {
        this.daoProgNac = daoProgNac;
        this.userInfoService = userInfoService;
        this.daoMeta = daoMeta;
    }

    @Override
    public DtoProgNac find(String codigo) {
        validateCodigo(codigo);
        return daoProgNac.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un Programa Nacional con el código especificado."));
    }

    @Override
    public List<DtoProgNac> findAll() {
        try {
            return daoProgNac.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los Programas Nacionales.", e);
        }
    }

    @Override
    public List<DtoProgNac> findAllActivos() {
        try {
            return daoProgNac.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Programas Nacionales activos.", e);
        }
    }

    @Override
    public Page<DtoProgNac> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoProgNac.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Programas Nacionales paginados.", e);
        }
    }

    @Override
    public Page<DtoProgNac> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            try {
                return daoProgNac.findByEstado(Estado.A, pageable).map(this::convertToDto);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener los Programas Nacionales paginados.", e);
            }
        } else {
            try {
                List<DtoProgNac> progNacs = daoProgNac.findByEstado(Estado.A).stream()
                        .map(this::convertToDto)
                        .toList();
                return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(progNacs, searchCriteria, pageable);
            } catch (Exception e) {
                throw new RuntimeException("Error al filtrar y paginar los Programas Nacionales activos.", e);
            }
        }
    }

    @Override
    public DtoProgNac save(DtoProgNac dtoProgNac, String accessToken) {
        validateDtoProgNac(dtoProgNac);

        Optional<ProgNac> progNacExistente = daoProgNac.findByCodigo(dtoProgNac.getCodigo());
        if (progNacExistente.isPresent()) {
            ProgNac progNac = progNacExistente.get();
            if (Estado.A.equals(progNac.getEstado())) {
                throw new DataValidationException("Ya existe un Programa Nacional activo con el código especificado.");
            } else {
                // Reactivar registro inactivo
                progNac.setEstado(Estado.A);
                progNac.setDescripcion(dtoProgNac.getDescripcion());
                progNac.setAlineacion(TipoAlineacion.ESTRATEGICA);
                progNac.setCodigoMetaFk(dtoProgNac.getCodigoMetaFk());
                progNac.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                progNac.setUsuarioCreacion(username);
                return convertToDto(daoProgNac.save(progNac));
            }
        }

        // Validar que exista la Meta referenciada
        if (!daoMeta.findByCodigo(dtoProgNac.getCodigoMetaFk()).isPresent()) {
            throw new DataValidationException("No existe una Meta con el código: " + dtoProgNac.getCodigoMetaFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        ProgNac progNac = new ProgNac();
        progNac.setCodigo(dtoProgNac.getCodigo());
        progNac.setDescripcion(dtoProgNac.getDescripcion());
        progNac.setEstado(Estado.A); // Nuevo registro siempre activo
        progNac.setAlineacion(TipoAlineacion.ESTRATEGICA);
        progNac.setCodigoMetaFk(dtoProgNac.getCodigoMetaFk());
        progNac.setFechaCreacion(new Date());
        progNac.setUsuarioCreacion(username);

        return convertToDto(daoProgNac.save(progNac));
    }

    @Override
    public DtoProgNac update(DtoProgNac dtoProgNac, String accessToken) {
        validateDtoProgNac(dtoProgNac);
        ProgNac progNacActual = daoProgNac.findByCodigo(dtoProgNac.getCodigo())
                .orElseThrow(() -> new DataValidationException("El Programa Nacional con el código especificado no existe."));

        // Validar que exista la Meta referenciada
        if (!daoMeta.findByCodigo(dtoProgNac.getCodigoMetaFk()).isPresent()) {
            throw new DataValidationException("No existe una Meta con el código: " + dtoProgNac.getCodigoMetaFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoProgNac.getEstado()) && Estado.A.equals(progNacActual.getEstado())) {
            throw new DataValidationException("Para desactivar un Programa Nacional use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        progNacActual.setDescripcion(dtoProgNac.getDescripcion());
        progNacActual.setEstado(Estado.A);
        progNacActual.setCodigoMetaFk(dtoProgNac.getCodigoMetaFk());
        progNacActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        progNacActual.setFechaModificacion(new Date());
        progNacActual.setUsuarioModificacion(username);

        validateProgNac(progNacActual);
        return convertToDto(daoProgNac.save(progNacActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        ProgNac progNac = daoProgNac.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un Programa Nacional con el código especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");
        progNac.setUsuarioModificacion(username);
        progNac.setFechaModificacion(new Date());
        progNac.setEstado(Estado.I);
        daoProgNac.save(progNac);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del Programa Nacional no puede ser nulo o vacío.");
        }
    }

    private void validateDtoProgNac(DtoProgNac dtoProgNac) {
        if (dtoProgNac == null) {
            throw new DataValidationException("DtoProgNac no puede ser nulo.");
        }
        validateCodigo(dtoProgNac.getCodigo());
        if (dtoProgNac.getDescripcion() == null || dtoProgNac.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoProgNac.getCodigoMetaFk() == null || dtoProgNac.getCodigoMetaFk().isEmpty()) {
            throw new DataValidationException("Código de Meta es requerido.");
        }
    }

    private void validateProgNac(ProgNac progNac) {
        if (progNac.getDescripcion() == null || progNac.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (progNac.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo o vacío");
        }
        if (progNac.getCodigoMetaFk() == null || progNac.getCodigoMetaFk().isEmpty()) {
            throw new DataValidationException("El código de Meta no puede ser nulo o vacío.");
        }
    }

    private DtoProgNac convertToDto(ProgNac progNac) {
        if (progNac == null) {
            return null;
        }
        DtoProgNac dto = new DtoProgNac();
        dto.setId(progNac.getId());
        dto.setCodigo(progNac.getCodigo());
        dto.setDescripcion(progNac.getDescripcion());
        dto.setEstado(progNac.getEstado());
        dto.setCodigoMetaFk(progNac.getCodigoMetaFk());
        dto.setAlineacion(progNac.getAlineacion());
        dto.setFechaCreacion(progNac.getFechaCreacion());
        dto.setUsuarioCreacion(progNac.getUsuarioCreacion());
        dto.setFechaModificacion(progNac.getFechaModificacion());
        dto.setUsuarioModificacion(progNac.getUsuarioModificacion());
        return dto;
    }
}