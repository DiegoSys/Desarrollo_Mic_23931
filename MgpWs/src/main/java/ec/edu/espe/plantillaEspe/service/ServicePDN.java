package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoOPN;
import ec.edu.espe.plantillaEspe.dao.DaoPDN;
import ec.edu.espe.plantillaEspe.dto.DtoPDN;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.PDN;
import ec.edu.espe.plantillaEspe.service.IService.IServicePDN;
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
public class ServicePDN implements IServicePDN {

    private final DaoPDN daoPDN;
    private final UserInfoService userInfoService;
    private final DaoOPN daoOPN;

    @Autowired
    public ServicePDN(DaoPDN daoPDN,
                      UserInfoService userInfoService,
                      DaoOPN daoOPN) {
        this.daoPDN = daoPDN;
        this.userInfoService = userInfoService;
        this.daoOPN = daoOPN;
    }

    @Override
    public DtoPDN find(String codigo) {
        validateCodigo(codigo);
        return daoPDN.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un PDN con el código especificado."));
    }

    @Override
    public List<DtoPDN> findAll() {
        try {
            return daoPDN.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los PDNs.", e);
        }
    }

    @Override
    public List<DtoPDN> findAllActivos() {
        try {
            return daoPDN.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los PDNs activos.", e);
        }
    }

    @Override
    public Page<DtoPDN> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPDN.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los PDNs paginados.", e);
        }
    }

    @Override
    public Page<DtoPDN> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPDN.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los PDNs paginados.", e);
        }
    }

    @Override
    public DtoPDN save(DtoPDN dtoPDN, String accessToken) {
        validateDtoPDN(dtoPDN);

        Optional<PDN> pdnExistente = daoPDN.findByCodigo(dtoPDN.getCodigo());
        if (pdnExistente.isPresent()) {
            PDN pdn = pdnExistente.get();
            if (Estado.A.equals(pdn.getEstado())) {
                throw new DataValidationException("Ya existe un PDN activo con el código especificado.");
            } else {
                // Reactivar registro inactivo
                pdn.setEstado(Estado.A);
                pdn.setDescripcion(dtoPDN.getDescripcion());
                pdn.setAlineacion(TipoAlineacion.ESTRATEGICA);
                pdn.setCodigoOpnFk(dtoPDN.getCodigoOpnFk());
                pdn.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                pdn.setUsuarioCreacion(username);
                return convertToDto(daoPDN.save(pdn));
            }
        }

        // Validar que exista el OPN referenciado
        if (!daoOPN.findByCodigo(dtoPDN.getCodigoOpnFk()).isPresent()) {
            throw new DataValidationException("No existe un OPN con el código: " + dtoPDN.getCodigoOpnFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        PDN pdn = new PDN();
        pdn.setCodigo(dtoPDN.getCodigo());
        pdn.setDescripcion(dtoPDN.getDescripcion());
        pdn.setEstado(Estado.A); // Nuevo registro siempre activo
        pdn.setAlineacion(TipoAlineacion.ESTRATEGICA);
        pdn.setCodigoOpnFk(dtoPDN.getCodigoOpnFk());
        pdn.setFechaCreacion(new Date());
        pdn.setUsuarioCreacion(username);

        return convertToDto(daoPDN.save(pdn));
    }

    @Override
    public DtoPDN update(DtoPDN dtoPDN, String accessToken) {
        validateDtoPDN(dtoPDN);
        PDN pdnActual = daoPDN.findByCodigo(dtoPDN.getCodigo())
                .orElseThrow(() -> new DataValidationException("El PDN con el código especificado no existe."));

        // Validar que exista el OPN referenciado
        if (!daoOPN.findByCodigo(dtoPDN.getCodigoOpnFk()).isPresent()) {
            throw new DataValidationException("No existe un OPN con el código: " + dtoPDN.getCodigoOpnFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoPDN.getEstado()) && Estado.A.equals(pdnActual.getEstado())) {
            throw new DataValidationException("Para desactivar un PDN use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        pdnActual.setDescripcion(dtoPDN.getDescripcion());
        pdnActual.setCodigoOpnFk(dtoPDN.getCodigoOpnFk());
        pdnActual.setEstado(Estado.A);
        pdnActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        pdnActual.setFechaModificacion(new Date());
        pdnActual.setUsuarioModificacion(username);

        validatePDN(pdnActual);
        return convertToDto(daoPDN.save(pdnActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        PDN pdn = daoPDN.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un PDN con el código especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        pdn.setEstado(Estado.I);
        pdn.setUsuarioModificacion(username);
        pdn.setFechaModificacion(new Date());
        daoPDN.save(pdn);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del PDN no puede ser nulo o vacío.");
        }
    }

    private void validateDtoPDN(DtoPDN dtoPDN) {
        if (dtoPDN == null) {
            throw new DataValidationException("DtoPDN no puede ser nulo");
        }
        validateCodigo(dtoPDN.getCodigo());
        if (dtoPDN.getDescripcion() == null || dtoPDN.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida");
        }
        if (dtoPDN.getCodigoOpnFk() == null || dtoPDN.getCodigoOpnFk().isEmpty()) {
            throw new DataValidationException("Código de OPN es requerido");
        }
    }

    private void validatePDN(PDN pdn) {
        if (pdn.getDescripcion() == null || pdn.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (pdn.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo.");
        }
        if (pdn.getCodigoOpnFk() == null || pdn.getCodigoOpnFk().isEmpty()) {
            throw new DataValidationException("El código de OPN no puede ser nulo o vacío.");
        }
    }

    private DtoPDN convertToDto(PDN pdn) {
        if (pdn == null) {
            return null;
        }
        DtoPDN dto = new DtoPDN();
        dto.setId(pdn.getId());
        dto.setCodigo(pdn.getCodigo());
        dto.setDescripcion(pdn.getDescripcion());
        dto.setEstado(pdn.getEstado());
        dto.setCodigoOpnFk(pdn.getCodigoOpnFk());
        dto.setAlineacion(pdn.getAlineacion());
        dto.setFechaCreacion(pdn.getFechaCreacion());
        dto.setUsuarioCreacion(pdn.getUsuarioCreacion());
        dto.setFechaModificacion(pdn.getFechaModificacion());
        dto.setUsuarioModificacion(pdn.getUsuarioModificacion());
        return dto;
    }
}