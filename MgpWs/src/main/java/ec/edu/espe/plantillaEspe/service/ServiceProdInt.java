package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoProdInt;
import ec.edu.espe.plantillaEspe.dao.DaoProgInst;
import ec.edu.espe.plantillaEspe.dto.DtoProdInt;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.ProdInt;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProdInt;
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
public class ServiceProdInt implements IServiceProdInt {

    private final DaoProdInt daoProdInt;
    private final UserInfoService userInfoService;
    private final DaoProgInst daoProgInst;

    @Autowired
    public ServiceProdInt(DaoProdInt daoProdInt,
                          UserInfoService userInfoService,
                          DaoProgInst daoProgInst) {
        this.daoProdInt = daoProdInt;
        this.userInfoService = userInfoService;
        this.daoProgInst = daoProgInst;
    }

    @Override
    public DtoProdInt find(String codigo) {
        validateCodigo(codigo);
        return daoProdInt.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un producto institucional con el código especificado."));
    }

    @Override
    public List<DtoProdInt> findAll() {
        try {
            return daoProdInt.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los Productos Institucionales.", e);
        }
    }

    @Override
    public List<DtoProdInt> findAllActivos() {
        try {
            return daoProdInt.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Productos Institucionales activos.", e);
        }
    }

    @Override
    public Page<DtoProdInt> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoProdInt.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Productos Institucionales paginados.", e);
        }
    }

    @Override
    public Page<DtoProdInt> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            try {
                return daoProdInt.findByEstado(Estado.A, pageable).map(this::convertToDto);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener los Productos Institucionales paginados.", e);
            }
        } else {
            try {
                List<DtoProdInt> productos = daoProdInt.findByEstado(Estado.A).stream()
                        .map(this::convertToDto)
                        .toList();
                return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(productos, searchCriteria, pageable);
            } catch (Exception e) {
                throw new RuntimeException("Error al filtrar y paginar los Productos Institucionales activos.", e);
            }
        }
    }

    @Override
    public DtoProdInt save(DtoProdInt dtoProdInt, String accessToken) {
        validateDtoProdInt(dtoProdInt);

        Optional<ProdInt> prodIntExistente = daoProdInt.findByCodigo(dtoProdInt.getCodigo());
        if (prodIntExistente.isPresent()) {
            ProdInt prodInt = prodIntExistente.get();
            if (Estado.A.equals(prodInt.getEstado())) {
                throw new DataValidationException("Ya existe un producto institucional activo con el código especificado.");
            } else {
                // Reactivar registro inactivo
                prodInt.setEstado(Estado.A);
                prodInt.setDescripcion(dtoProdInt.getDescripcion());
                prodInt.setAlineacion(TipoAlineacion.ESTRATEGICA);
                prodInt.setCodigoProginstFk(dtoProdInt.getCodigoProginstFk());
                prodInt.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                prodInt.setUsuarioCreacion(username);
                return convertToDto(daoProdInt.save(prodInt));
            }
        }

        // Validar que exista el Programa Institucional referenciado
        if (!daoProgInst.findByCodigo(dtoProdInt.getCodigoProginstFk()).isPresent()) {
            throw new DataValidationException("No existe un programa institucional con el código: " + dtoProdInt.getCodigoProginstFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        ProdInt prodInt = new ProdInt();
        prodInt.setCodigo(dtoProdInt.getCodigo());
        prodInt.setDescripcion(dtoProdInt.getDescripcion());
        prodInt.setEstado(Estado.A); // Nuevo registro siempre activo
        prodInt.setAlineacion(TipoAlineacion.ESTRATEGICA);
        prodInt.setCodigoProginstFk(dtoProdInt.getCodigoProginstFk());
        prodInt.setFechaCreacion(new Date());
        prodInt.setUsuarioCreacion(username);

        return convertToDto(daoProdInt.save(prodInt));
    }

    @Override
    public DtoProdInt update(DtoProdInt dtoProdInt, String accessToken) {
        validateDtoProdInt(dtoProdInt);
        ProdInt prodIntActual = daoProdInt.findByCodigo(dtoProdInt.getCodigo())
                .orElseThrow(() -> new DataValidationException("El producto institucional con el código especificado no existe."));

        // Validar que exista el Programa Institucional referenciado
        if (!daoProgInst.findByCodigo(dtoProdInt.getCodigoProginstFk()).isPresent()) {
            throw new DataValidationException("No existe un programa institucional con el código: " + dtoProdInt.getCodigoProginstFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoProdInt.getEstado()) && Estado.A.equals(prodIntActual.getEstado())) {
            throw new DataValidationException("Para desactivar un producto institucional use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        prodIntActual.setDescripcion(dtoProdInt.getDescripcion());
        prodIntActual.setEstado(Estado.A);
        prodIntActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        prodIntActual.setCodigoProginstFk(dtoProdInt.getCodigoProginstFk());
        prodIntActual.setFechaModificacion(new Date());
        prodIntActual.setUsuarioModificacion(username);

        validateProdInt(prodIntActual);
        return convertToDto(daoProdInt.save(prodIntActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        ProdInt prodInt = daoProdInt.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un producto institucional con el código especificado."));
        prodInt.setEstado(Estado.I);
        daoProdInt.save(prodInt);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del producto institucional no puede ser nulo o vacío.");
        }
    }

    private void validateDtoProdInt(DtoProdInt dtoProdInt) {
        if (dtoProdInt == null) {
            throw new DataValidationException("DtoProdInt no puede ser nulo.");
        }
        validateCodigo(dtoProdInt.getCodigo());
        if (dtoProdInt.getDescripcion() == null || dtoProdInt.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoProdInt.getEstado() == null ) {
            throw new DataValidationException("El estado es obligatorio.");
        }
        if (dtoProdInt.getCodigoProginstFk() == null || dtoProdInt.getCodigoProginstFk().isEmpty()) {
            throw new DataValidationException("Código de Programa Institucional es requerido.");
        }
    }

    private void validateProdInt(ProdInt prodInt) {
        if (prodInt.getDescripcion() == null || prodInt.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (prodInt.getEstado() == null ) {
            throw new DataValidationException("El estado no puede ser nulo o vacío.");
        }
        if (prodInt.getCodigoProginstFk() == null || prodInt.getCodigoProginstFk().isEmpty()) {
            throw new DataValidationException("El código de Programa Institucional no puede ser nulo o vacío.");
        }
    }

    private DtoProdInt convertToDto(ProdInt prodInt) {
        if (prodInt == null) {
            return null;
        }
        DtoProdInt dto = new DtoProdInt();
        dto.setId(prodInt.getId());
        dto.setCodigo(prodInt.getCodigo());
        dto.setDescripcion(prodInt.getDescripcion());
        dto.setCodigoProginstFk(prodInt.getCodigoProginstFk());
        dto.setAlineacion(prodInt.getAlineacion());
        dto.setEstado(prodInt.getEstado());
        dto.setFechaCreacion(prodInt.getFechaCreacion());
        dto.setUsuarioCreacion(prodInt.getUsuarioCreacion());
        dto.setFechaModificacion(prodInt.getFechaModificacion());
        dto.setUsuarioModificacion(prodInt.getUsuarioModificacion());
        return dto;
    }
}