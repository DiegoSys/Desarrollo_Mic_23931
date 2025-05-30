package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoOPN;
import ec.edu.espe.plantillaEspe.dao.DaoPlanNacional;
import ec.edu.espe.plantillaEspe.dto.DtoOPN;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.OPN;
import ec.edu.espe.plantillaEspe.service.IService.IServiceOPN;
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
public class ServiceOPN implements IServiceOPN {

    private final DaoOPN daoOPN;
    private final UserInfoService userInfoService;
    private final DaoPlanNacional daoPlanNacional;

    @Autowired
    public ServiceOPN(DaoOPN daoOPN,
                      UserInfoService userInfoService,
                      DaoPlanNacional daoPlanNacional) {
        this.daoOPN = daoOPN;
        this.userInfoService = userInfoService;
        this.daoPlanNacional = daoPlanNacional;
    }

    @Override
    public DtoOPN find(String codigo) {
        validateCodigo(codigo);
        return daoOPN.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un OPN con el código especificado."));
    }

    @Override
    public List<DtoOPN> findAll() {
        try {
            return daoOPN.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los OPNs.", e);
        }
    }

    @Override
    public List<DtoOPN> findAllActivos() {
        try {
            return daoOPN.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los OPNs activos.", e);
        }
    }

    @Override
    public Page<DtoOPN> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoOPN.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los OPNs paginados.", e);
        }
    }

    @Override
    public Page<DtoOPN> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoOPN.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los OPNs paginados.", e);
        }
    }

    @Override
    public DtoOPN save(DtoOPN dtoOPN, String accessToken) {
        validateDtoOPN(dtoOPN);

        Optional<OPN> opnExistente = daoOPN.findByCodigo(dtoOPN.getCodigo());
        if (opnExistente.isPresent()) {
            OPN opn = opnExistente.get();
            if (Estado.A.equals(opn.getEstado())) {
                throw new DataValidationException("Ya existe un OPN activo con el código especificado.");
            } else {
                // Reactivar registro inactivo
                opn.setEstado(Estado.A);
                opn.setDescripcion(dtoOPN.getDescripcion());
                opn.setAlineacion(TipoAlineacion.ESTRATEGICA);
                opn.setCodigoPlanNacionalFk(dtoOPN.getCodigoPlanNacionalFk());
                opn.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                opn.setUsuarioCreacion(username);
                return convertToDto(daoOPN.save(opn));
            }
        }

        // Validar que exista el Plan Nacional referenciado
        if (!daoPlanNacional.findByCodigo(dtoOPN.getCodigoPlanNacionalFk()).isPresent()) {
            throw new DataValidationException("No existe un plan nacional con el código: " + dtoOPN.getCodigoPlanNacionalFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        OPN opn = new OPN();
        opn.setCodigo(dtoOPN.getCodigo());
        opn.setDescripcion(dtoOPN.getDescripcion());
        opn.setEstado(Estado.A); // Nuevo registro siempre activo
        opn.setAlineacion(TipoAlineacion.ESTRATEGICA);
        opn.setCodigoPlanNacionalFk(dtoOPN.getCodigoPlanNacionalFk());
        opn.setFechaCreacion(new Date());
        opn.setUsuarioCreacion(username);

        return convertToDto(daoOPN.save(opn));
    }

    @Override
    public DtoOPN update(DtoOPN dtoOPN, String accessToken) {
        validateDtoOPN(dtoOPN);
        OPN opnActual = daoOPN.findByCodigo(dtoOPN.getCodigo())
                .orElseThrow(() -> new DataValidationException("El OPN con el código especificado no existe."));

        // Validar que exista el Plan Nacional referenciado
        if (!daoPlanNacional.findByCodigo(dtoOPN.getCodigoPlanNacionalFk()).isPresent()) {
            throw new DataValidationException("No existe un plan nacional con el código: " + dtoOPN.getCodigoPlanNacionalFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoOPN.getEstado()) && Estado.A.equals(opnActual.getEstado())) {
            throw new DataValidationException("Para desactivar un OPN use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        opnActual.setDescripcion(dtoOPN.getDescripcion());
        opnActual.setCodigoPlanNacionalFk(dtoOPN.getCodigoPlanNacionalFk());
        opnActual.setEstado(Estado.A);
        opnActual.setFechaModificacion(new Date());
        opnActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        opnActual.setUsuarioModificacion(username);

        validateOPN(opnActual);
        return convertToDto(daoOPN.save(opnActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código no puede ser nulo o vacío.");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        OPN opn = daoOPN.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un OPN con el código especificado."));
        opn.setEstado(Estado.I);
        opn.setFechaModificacion(new Date());
        opn.setUsuarioModificacion(username);
        daoOPN.save(opn);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del OPN no puede ser nulo o vacío.");
        }
    }

    private void validateDtoOPN(DtoOPN dtoOPN) {
        if (dtoOPN == null) {
            throw new DataValidationException("DtoOPN no puede ser nulo");
        }
        validateCodigo(dtoOPN.getCodigo());
        if (dtoOPN.getDescripcion() == null || dtoOPN.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoOPN.getCodigoPlanNacionalFk() == null || dtoOPN.getCodigoPlanNacionalFk().isEmpty()) {
            throw new DataValidationException("Código de Plan Nacional es requerido.");
        }
    }

    private void validateOPN(OPN opn) {
        if (opn.getDescripcion() == null || opn.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (opn.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo.");
        }
        if (opn.getCodigoPlanNacionalFk() == null || opn.getCodigoPlanNacionalFk().isEmpty()) {
            throw new DataValidationException("El código de Plan Nacional no puede ser nulo o vacío.");
        }
    }

    private DtoOPN convertToDto(OPN opn) {
        if (opn == null) {
            return null;
        }
        DtoOPN dto = new DtoOPN();
        dto.setId(opn.getId());
        dto.setCodigo(opn.getCodigo());
        dto.setDescripcion(opn.getDescripcion());
        dto.setEstado(opn.getEstado());
        dto.setCodigoPlanNacionalFk(opn.getCodigoPlanNacionalFk());
        dto.setFechaCreacion(opn.getFechaCreacion());
        dto.setAlineacion(opn.getAlineacion());
        dto.setUsuarioCreacion(opn.getUsuarioCreacion());
        dto.setFechaModificacion(opn.getFechaModificacion());
        dto.setUsuarioModificacion(opn.getUsuarioModificacion());
        return dto;
    }

}