package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoObjEstrategico;
import ec.edu.espe.plantillaEspe.dao.DaoObjOperativo;
import ec.edu.espe.plantillaEspe.dto.DtoObjOperativo;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.ObjOperativo;
import ec.edu.espe.plantillaEspe.service.IService.IServiceObjOperativo;
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
public class ServiceObjOperativo implements IServiceObjOperativo {

    private final DaoObjOperativo daoObjOperativo;
    private final UserInfoService userInfoService;
    private final DaoObjEstrategico daoObjEstrategico;

    @Autowired
    public ServiceObjOperativo(DaoObjOperativo daoObjOperativo,
                              UserInfoService userInfoService,
                               DaoObjEstrategico daoObjEstrategico) {
        this.daoObjOperativo = daoObjOperativo;
        this.userInfoService = userInfoService;
        this.daoObjEstrategico = daoObjEstrategico;
    }

    @Override
    public DtoObjOperativo find(String codigo) {
        validateCodigo(codigo);
        return daoObjOperativo.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un Objetivo Operativo con el código especificado."));
    }

    @Override
    public List<DtoObjOperativo> findAll() {
        try {
            return daoObjOperativo.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los Objetivos Operativos.", e);
        }
    }

    @Override
    public List<DtoObjOperativo> findAllActivos() {
        try {
            return daoObjOperativo.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Objetivos Operativos activos.", e);
        }
    }

    @Override
    public Page<DtoObjOperativo> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoObjOperativo.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Objetivos Operativos paginados.", e);
        }
    }

    @Override
    public Page<DtoObjOperativo> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoObjOperativo.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los Objetivos Operativos paginados.", e);
        }
    }

    @Override
    public DtoObjOperativo save(DtoObjOperativo dto, String accessToken) {
        validateDtoObjOperativo(dto);

        Optional<ObjOperativo> objOperativoExistente = daoObjOperativo.findByCodigo(dto.getCodigo());
        if (objOperativoExistente.isPresent()) {
            ObjOperativo objOperativo = objOperativoExistente.get();
            if (Estado.A.equals(objOperativo.getEstado())) {
                throw new DataValidationException("Ya existe un Objetivo Operativo activo con el código especificado.");
            } else {
                // Reactivar registro inactivo
                objOperativo.setEstado(Estado.A);
                objOperativo.setDescripcion(dto.getDescripcion());
                objOperativo.setAlineacion(TipoAlineacion.ESTRATEGICA);
                objOperativo.setCodigoEstrFk(dto.getCodigoEstrFk());
                objOperativo.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                objOperativo.setUsuarioCreacion(username);
                return convertToDto(daoObjOperativo.save(objOperativo));
            }
        }

        //System.out.println(dto.getCodigoEstrFk());
        // Validar que exista la Estrategia referenciada
        if (!daoObjEstrategico.findByCodigo(dto.getCodigoEstrFk()).isPresent()) {
            throw new DataValidationException("No existe un Objetivo Estratégico con el código: " + dto.getCodigoEstrFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        ObjOperativo objOperativo = new ObjOperativo();
        objOperativo.setCodigo(dto.getCodigo());
        objOperativo.setDescripcion(dto.getDescripcion());
        objOperativo.setEstado(Estado.A); // Nuevo registro siempre activo
        objOperativo.setAlineacion(TipoAlineacion.ESTRATEGICA);
        objOperativo.setCodigoEstrFk(dto.getCodigoEstrFk());
        objOperativo.setFechaCreacion(new Date());
        objOperativo.setUsuarioCreacion(username);

        return convertToDto(daoObjOperativo.save(objOperativo));
    }

    @Override
    public DtoObjOperativo update(DtoObjOperativo dto, String accessToken) {
        validateDtoObjOperativo(dto);
        ObjOperativo objOperativoActual = daoObjOperativo.findByCodigo(dto.getCodigo())
                .orElseThrow(() -> new DataValidationException("El Objetivo Operativo con el código especificado no existe."));

        // Validar que exista la Estrategia referenciada
        if (!daoObjEstrategico.findByCodigo(dto.getCodigoEstrFk()).isPresent()) {
            throw new DataValidationException("No existe un Objetivo Estratégico con el código: " + dto.getCodigoEstrFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dto.getEstado()) && Estado.A.equals(objOperativoActual.getEstado())) {
            throw new DataValidationException("Para desactivar un Objetivo Operativo use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        objOperativoActual.setDescripcion(dto.getDescripcion());
        objOperativoActual.setEstado(Estado.A);
        objOperativoActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        objOperativoActual.setCodigoEstrFk(dto.getCodigoEstrFk());
        objOperativoActual.setFechaModificacion(new Date());
        objOperativoActual.setUsuarioModificacion(username);

        validateObjOperativo(objOperativoActual);
        return convertToDto(daoObjOperativo.save(objOperativoActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        ObjOperativo objOperativo = daoObjOperativo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un Objetivo Operativo con el código especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        objOperativo.setUsuarioModificacion(username);
        objOperativo.setUsuarioModificacion(username);
        objOperativo.setEstado(Estado.I);
        daoObjOperativo.save(objOperativo);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del Objetivo Operativo no puede ser nulo o vacío.");
        }
    }

    private void validateDtoObjOperativo(DtoObjOperativo dto) {
        if (dto == null) {
            throw new DataValidationException("DtoObjOperativo no puede ser nulo.");
        }
        validateCodigo(dto.getCodigo());
        if (dto.getDescripcion() == null || dto.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dto.getCodigoEstrFk() == null || dto.getCodigoEstrFk().isEmpty()) {
            throw new DataValidationException("Código de Estrategia es requerido.");
        }
    }

    private void validateObjOperativo(ObjOperativo objOperativo) {
        if (objOperativo.getDescripcion() == null || objOperativo.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (objOperativo.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo o vacío.");
        }
        if (objOperativo.getCodigoEstrFk() == null || objOperativo.getCodigoEstrFk().isEmpty()) {
            throw new DataValidationException("El código de Estrategia no puede ser nulo o vacío.");
        }
    }

    private DtoObjOperativo convertToDto(ObjOperativo objOperativo) {
        if (objOperativo == null) {
            return null;
        }
        DtoObjOperativo dto = new DtoObjOperativo();
        dto.setId(objOperativo.getId());
        dto.setCodigo(objOperativo.getCodigo());
        dto.setDescripcion(objOperativo.getDescripcion());
        dto.setEstado(objOperativo.getEstado());
        dto.setCodigoEstrFk(objOperativo.getCodigoEstrFk());
        dto.setAlineacion(objOperativo.getAlineacion());
        dto.setFechaCreacion(objOperativo.getFechaCreacion());
        dto.setUsuarioCreacion(objOperativo.getUsuarioCreacion());
        dto.setFechaModificacion(objOperativo.getFechaModificacion());
        dto.setUsuarioModificacion(objOperativo.getUsuarioModificacion());
        return dto;
    }
}