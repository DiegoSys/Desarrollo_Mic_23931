package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoObjDesSost;
import ec.edu.espe.plantillaEspe.dto.DtoObjDesSost;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.model.ObjDesSost;
import ec.edu.espe.plantillaEspe.service.IService.IServiceObjDesSost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceObjDesSost implements IServiceObjDesSost {

    private final DaoObjDesSost daoObjDesSost;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceObjDesSost(DaoObjDesSost daoObjDesSost, UserInfoService userInfoService) {
        this.daoObjDesSost = daoObjDesSost;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoObjDesSost find(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código no puede ser nulo o vacío.");
        }

        ObjDesSost objDesSost = daoObjDesSost.findByCodigo(codigo)
                .orElseThrow(() -> new NotFoundException("Objetivo de desarrollo sostenible no encontrado."));

        return mapToDto(objDesSost);
    }

    @Override
    public List<DtoObjDesSost> findAllActivos() {
        try {
            return daoObjDesSost.findByEstado(Estado.A).stream()
                    .map(this::mapToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los Objetivo de desarrollo sostenible activos.", e);
        }
    }

    @Override
    public Page<DtoObjDesSost> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoObjDesSost.findByEstado(Estado.A, pageable)
                    .map(this::mapToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Objetivo de desarrollo sostenible activos paginados.", e);
        }
    }

    @Override
    public DtoObjDesSost save(DtoObjDesSost dtoObjDesSost, String accessToken) {
        validateDto(dtoObjDesSost);

        Optional<ObjDesSost> existingObj = daoObjDesSost.findByCodigo(dtoObjDesSost.getCodigo());

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        if (existingObj.isPresent()) {
            ObjDesSost objExistente = existingObj.get();
            if (objExistente.getEstado() == Estado.A) {
                throw new DataValidationException("Ya existe un objetivo activo con el código: " + dtoObjDesSost.getCodigo());
            }
            // Reactivar registro inactivo
            objExistente.setDescripcion(dtoObjDesSost.getDescripcion());
            objExistente.setAlineacion(TipoAlineacion.ESTRATEGICA);
            objExistente.setEstado(Estado.A);
            objExistente.setUsuarioCreacion(username);
            objExistente.setFechaCreacion(new Date());

            return mapToDto(daoObjDesSost.save(objExistente));
        }

        // Crear nuevo objeto
        ObjDesSost objDesSost = new ObjDesSost();
        objDesSost.setCodigo(dtoObjDesSost.getCodigo());
        objDesSost.setDescripcion(dtoObjDesSost.getDescripcion());
        objDesSost.setEstado(Estado.A); // Siempre se crea como activo
        objDesSost.setAlineacion(TipoAlineacion.ESTRATEGICA);
        objDesSost.setUsuarioCreacion(username);
        objDesSost.setFechaCreacion(new Date());

        return mapToDto(daoObjDesSost.save(objDesSost));
    }

    @Override
    public DtoObjDesSost update(DtoObjDesSost dtoObjDesSost, String accessToken) {
        validateDto(dtoObjDesSost);

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        ObjDesSost existingObj = daoObjDesSost.findByCodigo(dtoObjDesSost.getCodigo())
                .orElseThrow(() -> new NotFoundException("Objetivo de desarrollo sostenible no encontrado."));

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoObjDesSost.getEstado()) && Estado.A.equals(existingObj.getEstado())) {
            throw new DataValidationException("Para desactivar un objetivo de desarrollo sostenible use el método delete()");
        }

        existingObj.setDescripcion(dtoObjDesSost.getDescripcion());
        existingObj.setEstado(Estado.A);
        existingObj.setAlineacion(TipoAlineacion.ESTRATEGICA);
        existingObj.setUsuarioModificacion(username);
        existingObj.setFechaModificacion(new Date());

        validateObjDesSost(existingObj);
        ObjDesSost updatedObj = daoObjDesSost.save(existingObj);
        return mapToDto(updatedObj);
    }

    @Override
    public void delete(String codigo, String accessToken) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código no puede ser nulo o vacío.");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        ObjDesSost objDesSost = daoObjDesSost.findByCodigo(codigo)
                .orElseThrow(() -> new NotFoundException("Objetivo de desarrollo sostenible no encontrado."));

        objDesSost.setEstado(Estado.I);
        objDesSost.setUsuarioModificacion(username);
        objDesSost.setFechaModificacion(new Date());
        daoObjDesSost.save(objDesSost);
    }

    private DtoObjDesSost mapToDto(ObjDesSost objDesSost) {
        DtoObjDesSost dto = new DtoObjDesSost();
        dto.setId(objDesSost.getId());
        dto.setCodigo(objDesSost.getCodigo());
        dto.setDescripcion(objDesSost.getDescripcion());
        dto.setEstado(objDesSost.getEstado());
        dto.setAlineacion(objDesSost.getAlineacion());
        dto.setUsuarioCreacion(objDesSost.getUsuarioCreacion());
        dto.setFechaCreacion(objDesSost.getFechaCreacion());
        dto.setUsuarioModificacion(objDesSost.getUsuarioModificacion());
        dto.setFechaModificacion(objDesSost.getFechaModificacion());
        return dto;
    }

    private void validateDto(DtoObjDesSost dto) {
        if (dto.getCodigo() == null || dto.getCodigo().isEmpty()) {
            throw new DataValidationException("El código es obligatorio.");
        }
        if (dto.getDescripcion() == null || dto.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción es obligatoria.");
        }

    }

    private void validateObjDesSost(ObjDesSost objDesSost) {
        if (objDesSost.getDescripcion() == null || objDesSost.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (objDesSost.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo.");
        }

    }
}