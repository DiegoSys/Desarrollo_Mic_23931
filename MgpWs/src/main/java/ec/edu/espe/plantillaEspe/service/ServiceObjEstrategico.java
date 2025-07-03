package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoMeta;
import ec.edu.espe.plantillaEspe.dao.DaoObjEstrategico;
import ec.edu.espe.plantillaEspe.dto.DtoObjEstrategico;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.ObjEstrategico;
import ec.edu.espe.plantillaEspe.service.IService.IServiceObjEstrategico;
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
public class ServiceObjEstrategico implements IServiceObjEstrategico {

    private final DaoObjEstrategico daoObjEstrategico;
    private final UserInfoService userInfoService;
    private final DaoMeta daoMeta;

    @Autowired
    public ServiceObjEstrategico(DaoObjEstrategico daoObjEstrategico,
                                 UserInfoService userInfoService,
                                 DaoMeta daoMeta) {
        this.daoObjEstrategico = daoObjEstrategico;
        this.userInfoService = userInfoService;
        this.daoMeta = daoMeta;
    }

    @Override
    public DtoObjEstrategico find(String codigo) {
        validateCodigo(codigo);
        return daoObjEstrategico.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un Objetivo Estratégico con el código especificado."));
    }

    @Override
    public List<DtoObjEstrategico> findAll() {
        try {
            return daoObjEstrategico.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los Objetivos Estratégicos.", e);
        }
    }

    @Override
    public List<DtoObjEstrategico> findAllActivos() {
        try {
            return daoObjEstrategico.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Objetivos Estratégicos activos.", e);
        }
    }

    @Override
    public Page<DtoObjEstrategico> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoObjEstrategico.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Objetivos Estratégicos paginados.", e);
        }
    }

    @Override
    public Page<DtoObjEstrategico> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            try {
                return daoObjEstrategico.findByEstado(Estado.A, pageable).map(this::convertToDto);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener los Objetivos Estratégicos paginados.", e);
            }
        } else {
            try {
                List<DtoObjEstrategico> objetivos = daoObjEstrategico.findByEstado(Estado.A).stream()
                        .map(this::convertToDto)
                        .toList();
                return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(objetivos, searchCriteria, pageable);
            } catch (Exception e) {
                throw new RuntimeException("Error al filtrar y paginar los Objetivos Estratégicos activos.", e);
            }
        }
    }

    @Override
    public DtoObjEstrategico save(DtoObjEstrategico dtoObjEstrategico, String accessToken) {
        validateDtoObjEstrategico(dtoObjEstrategico);

        Optional<ObjEstrategico> objEstrategicoExistente = daoObjEstrategico.findByCodigo(dtoObjEstrategico.getCodigo());
        if (objEstrategicoExistente.isPresent()) {
            ObjEstrategico objEstrategico = objEstrategicoExistente.get();
            if (Estado.A.equals(objEstrategico.getEstado())) {
                throw new DataValidationException("Ya existe un Objetivo Estratégico activo con el código especificado.");
            } else {
                // Reactivar registro inactivo
                objEstrategico.setEstado(Estado.A);
                objEstrategico.setDescripcion(dtoObjEstrategico.getDescripcion());
                objEstrategico.setAlineacion(TipoAlineacion.ESTRATEGICA);
                objEstrategico.setCodigoMetaFk(dtoObjEstrategico.getCodigoMetaFk());
                objEstrategico.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                objEstrategico.setUsuarioCreacion(username);
                return convertToDto(daoObjEstrategico.save(objEstrategico));
            }
        }

        // Validar que exista la Meta referenciada
        if (!daoMeta.findByCodigo(dtoObjEstrategico.getCodigoMetaFk()).isPresent()) {
            throw new DataValidationException("No existe una Meta con el código: " + dtoObjEstrategico.getCodigoMetaFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        ObjEstrategico objEstrategico = new ObjEstrategico();
        objEstrategico.setCodigo(dtoObjEstrategico.getCodigo());
        objEstrategico.setDescripcion(dtoObjEstrategico.getDescripcion());
        objEstrategico.setEstado(Estado.A); // Nuevo registro siempre activo
        objEstrategico.setAlineacion(TipoAlineacion.ESTRATEGICA);
        objEstrategico.setCodigoMetaFk(dtoObjEstrategico.getCodigoMetaFk());
        objEstrategico.setFechaCreacion(new Date());
        objEstrategico.setUsuarioCreacion(username);

        return convertToDto(daoObjEstrategico.save(objEstrategico));
    }

    @Override
    public DtoObjEstrategico update(DtoObjEstrategico dtoObjEstrategico, String accessToken) {
        validateDtoObjEstrategico(dtoObjEstrategico);
        ObjEstrategico objEstrategicoActual = daoObjEstrategico.findByCodigo(dtoObjEstrategico.getCodigo())
                .orElseThrow(() -> new DataValidationException("El Objetivo Estratégico con el código especificado no existe."));

        // Validar que exista la Meta referenciada
        if (!daoMeta.findByCodigo(dtoObjEstrategico.getCodigoMetaFk()).isPresent()) {
            throw new DataValidationException("No existe una Meta con el código: " + dtoObjEstrategico.getCodigoMetaFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoObjEstrategico.getEstado()) && Estado.A.equals(objEstrategicoActual.getEstado())) {
            throw new DataValidationException("Para desactivar un Objetivo Estratégico use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        objEstrategicoActual.setDescripcion(dtoObjEstrategico.getDescripcion());
        objEstrategicoActual.setEstado(Estado.A);
        objEstrategicoActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        objEstrategicoActual.setCodigoMetaFk(dtoObjEstrategico.getCodigoMetaFk());
        objEstrategicoActual.setFechaModificacion(new Date());
        objEstrategicoActual.setUsuarioModificacion(username);

        validateObjEstrategico(objEstrategicoActual);
        return convertToDto(daoObjEstrategico.save(objEstrategicoActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        ObjEstrategico objEstrategico = daoObjEstrategico.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un Objetivo Estratégico con el código especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        objEstrategico.setUsuarioModificacion(username);
        objEstrategico.setFechaModificacion(new Date());
        objEstrategico.setEstado(Estado.I);
        daoObjEstrategico.save(objEstrategico);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del Objetivo Estratégico no puede ser nulo o vacío.");
        }
    }

    private void validateDtoObjEstrategico(DtoObjEstrategico dtoObjEstrategico) {
        if (dtoObjEstrategico == null) {
            throw new DataValidationException("DtoObjEstrategico no puede ser nulo");
        }
        validateCodigo(dtoObjEstrategico.getCodigo());
        if (dtoObjEstrategico.getDescripcion() == null || dtoObjEstrategico.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoObjEstrategico.getCodigoMetaFk() == null || dtoObjEstrategico.getCodigoMetaFk().isEmpty()) {
            throw new DataValidationException("Código de Meta es requerido.");
        }
    }

    private void validateObjEstrategico(ObjEstrategico objEstrategico) {
        if (objEstrategico.getDescripcion() == null || objEstrategico.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (objEstrategico.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo.");
        }
        if (objEstrategico.getCodigoMetaFk() == null || objEstrategico.getCodigoMetaFk().isEmpty()) {
            throw new DataValidationException("El código de Meta no puede ser nulo o vacío.");
        }
    }

    private DtoObjEstrategico convertToDto(ObjEstrategico objEstrategico) {
        if (objEstrategico == null) {
            return null;
        }
        DtoObjEstrategico dto = new DtoObjEstrategico();
        dto.setId(objEstrategico.getId());
        dto.setCodigo(objEstrategico.getCodigo());
        dto.setDescripcion(objEstrategico.getDescripcion());
        dto.setEstado(objEstrategico.getEstado());
        dto.setAlineacion(objEstrategico.getAlineacion());
        dto.setCodigoMetaFk(objEstrategico.getCodigoMetaFk());
        dto.setFechaCreacion(objEstrategico.getFechaCreacion());
        dto.setUsuarioCreacion(objEstrategico.getUsuarioCreacion());
        dto.setFechaModificacion(objEstrategico.getFechaModificacion());
        dto.setUsuarioModificacion(objEstrategico.getUsuarioModificacion());
        return dto;
    }
}