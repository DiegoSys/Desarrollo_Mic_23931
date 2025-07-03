package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoEstrategia;
import ec.edu.espe.plantillaEspe.dao.DaoPDN;
import ec.edu.espe.plantillaEspe.dto.DtoEstrategia;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Estrategia;
import ec.edu.espe.plantillaEspe.service.IService.IServiceEstrategia;
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
public class ServiceEstrategia implements IServiceEstrategia {

    private final DaoEstrategia daoEstrategia;
    private final UserInfoService userInfoService;
    private final DaoPDN daoPDN;

    @Autowired
    public ServiceEstrategia(DaoEstrategia daoEstrategia,
                             UserInfoService userInfoService,
                             DaoPDN daoPDN) {
        this.daoEstrategia = daoEstrategia;
        this.userInfoService = userInfoService;
        this.daoPDN = daoPDN;
    }

    @Override
    public DtoEstrategia find(String codigo) {
        validateCodigo(codigo);
        return daoEstrategia.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró una Estrategia con el código especificado."));
    }

    @Override
    public List<DtoEstrategia> findAll() {
        try {
            return daoEstrategia.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todas las Estrategias.", e);
        }
    }

    @Override
    public List<DtoEstrategia> findAllActivos() {
        try {
            return daoEstrategia.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las Estrategias activas.", e);
        }
    }

    @Override
    public Page<DtoEstrategia> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoEstrategia.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las Estrategias paginadas.", e);
        }
    }

    @Override
    public Page<DtoEstrategia> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            try {
                return daoEstrategia.findByEstado(Estado.A, pageable).map(this::convertToDto);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener las Estrategias paginadas.", e);
            }
        } else {
            try {
                List<DtoEstrategia> estrategias = daoEstrategia.findByEstado(Estado.A).stream()
                        .map(this::convertToDto)
                        .toList();
                return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(estrategias, searchCriteria, pageable);
            } catch (Exception e) {
                throw new RuntimeException("Error al filtrar y paginar las Estrategias activas.", e);
            }
        }
    }

    @Override
    public DtoEstrategia save(DtoEstrategia dtoEstrategia, String accessToken) {
        validateDtoEstrategia(dtoEstrategia);

        Optional<Estrategia> estrategiaExistente = daoEstrategia.findByCodigo(dtoEstrategia.getCodigo());
        if (estrategiaExistente.isPresent()) {
            Estrategia estrategia = estrategiaExistente.get();
            if (Estado.A.equals(estrategia.getEstado())) {
                throw new DataValidationException("Ya existe una Estrategia activa con el código especificado.");
            } else {
                // Reactivar registro inactivo
                estrategia.setEstado(Estado.A);
                estrategia.setDescripcion(dtoEstrategia.getDescripcion());
                estrategia.setAlineacion(TipoAlineacion.ESTRATEGICA);
                estrategia.setCodigoPdnFk(dtoEstrategia.getCodigoPdnFk());
                estrategia.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                estrategia.setUsuarioCreacion(username);
                return convertToDto(daoEstrategia.save(estrategia));
            }
        }

        // Validar que exista el PDN referenciado
        if (!daoPDN.findByCodigo(dtoEstrategia.getCodigoPdnFk()).isPresent()) {
            throw new DataValidationException("No existe un PDN con el código: " + dtoEstrategia.getCodigoPdnFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        Estrategia estrategia = new Estrategia();
        estrategia.setCodigo(dtoEstrategia.getCodigo());
        estrategia.setDescripcion(dtoEstrategia.getDescripcion());
        estrategia.setAlineacion(TipoAlineacion.ESTRATEGICA);
        estrategia.setEstado(Estado.A); // Nuevo registro siempre activo
        estrategia.setCodigoPdnFk(dtoEstrategia.getCodigoPdnFk());
        estrategia.setFechaCreacion(new Date());
        estrategia.setUsuarioCreacion(username);

        return convertToDto(daoEstrategia.save(estrategia));
    }

    @Override
    public DtoEstrategia update(DtoEstrategia dtoEstrategia, String accessToken) {
        validateDtoEstrategia(dtoEstrategia);
        Estrategia estrategiaActual = daoEstrategia.findByCodigo(dtoEstrategia.getCodigo())
                .orElseThrow(() -> new DataValidationException("La Estrategia con el código especificado no existe."));

        // Validar que exista el PDN referenciado
        if (!daoPDN.findByCodigo(dtoEstrategia.getCodigoPdnFk()).isPresent()) {
            throw new DataValidationException("No existe un PDN con el código: " + dtoEstrategia.getCodigoPdnFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoEstrategia.getEstado()) && Estado.A.equals(estrategiaActual.getEstado())) {
            throw new DataValidationException("Para desactivar una Estrategia use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        estrategiaActual.setDescripcion(dtoEstrategia.getDescripcion());
        estrategiaActual.setEstado(Estado.A);
        estrategiaActual.setCodigoPdnFk(dtoEstrategia.getCodigoPdnFk());
        estrategiaActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        estrategiaActual.setFechaModificacion(new Date());
        estrategiaActual.setUsuarioModificacion(username);

        validateEstrategia(estrategiaActual);
        return convertToDto(daoEstrategia.save(estrategiaActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        Estrategia estrategia = daoEstrategia.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró una Estrategia con el código especificado."));
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        estrategia.setUsuarioModificacion(username);
        estrategia.setFechaModificacion(new Date());
        estrategia.setEstado(Estado.I);
        daoEstrategia.save(estrategia);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código de la Estrategia no puede ser nulo o vacío.");
        }
    }

    private void validateDtoEstrategia(DtoEstrategia dtoEstrategia) {
        if (dtoEstrategia == null) {
            throw new DataValidationException("DtoEstrategia no puede ser nulo.");
        }
        validateCodigo(dtoEstrategia.getCodigo());
        if (dtoEstrategia.getDescripcion() == null || dtoEstrategia.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoEstrategia.getCodigoPdnFk() == null || dtoEstrategia.getCodigoPdnFk().isEmpty()) {
            throw new DataValidationException("Código de PDN es requerido.");
        }
    }

    private void validateEstrategia(Estrategia estrategia) {
        if (estrategia.getDescripcion() == null || estrategia.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (estrategia.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo.");
        }
        if (estrategia.getCodigoPdnFk() == null || estrategia.getCodigoPdnFk().isEmpty()) {
            throw new DataValidationException("El código de PDN no puede ser nulo o vacío.");
        }
    }

    private DtoEstrategia convertToDto(Estrategia estrategia) {
        if (estrategia == null) {
            return null;
        }
        DtoEstrategia dto = new DtoEstrategia();
        dto.setId(estrategia.getId());
        dto.setCodigo(estrategia.getCodigo());
        dto.setDescripcion(estrategia.getDescripcion());
        dto.setEstado(estrategia.getEstado());
        dto.setAlineacion(estrategia.getAlineacion());
        dto.setCodigoPdnFk(estrategia.getCodigoPdnFk());
        dto.setFechaCreacion(estrategia.getFechaCreacion());
        dto.setUsuarioCreacion(estrategia.getUsuarioCreacion());
        dto.setFechaModificacion(estrategia.getFechaModificacion());
        dto.setUsuarioModificacion(estrategia.getUsuarioModificacion());
        return dto;
    }
}