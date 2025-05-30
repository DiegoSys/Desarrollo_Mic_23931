package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoMeta;
import ec.edu.espe.plantillaEspe.dao.DaoOPN;
import ec.edu.espe.plantillaEspe.dto.DtoMeta;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Meta;
import ec.edu.espe.plantillaEspe.service.IService.IServiceMeta;
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
public class ServiceMeta implements IServiceMeta {

    private final DaoMeta daoMeta;
    private final UserInfoService userInfoService;
    private final DaoOPN daoOPN;

    @Autowired
    public ServiceMeta(DaoMeta daoMeta,
                       UserInfoService userInfoService,
                       DaoOPN daoOPN) {
        this.daoMeta = daoMeta;
        this.userInfoService = userInfoService;
        this.daoOPN = daoOPN;
    }

    @Override
    public DtoMeta find(String codigo) {
        validateCodigo(codigo);
        return daoMeta.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró una Meta con el código especificado."));
    }

    @Override
    public List<DtoMeta> findAll() {
        try {
            return daoMeta.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todas las metas.", e);
        }
    }

    @Override
    public List<DtoMeta> findAllActivos() {
        try {
            return daoMeta.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las metas activas.", e);
        }
    }

    @Override
    public Page<DtoMeta> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoMeta.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las Metas paginadas.", e);
        }
    }

    @Override
    public Page<DtoMeta> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoMeta.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las Metas paginadas.", e);
        }
    }

    @Override
    public DtoMeta save(DtoMeta dtoMeta, String accessToken) {
        validateDtoMeta(dtoMeta);

        Optional<Meta> metaExistente = daoMeta.findByCodigo(dtoMeta.getCodigo());
        if (metaExistente.isPresent()) {
            Meta meta = metaExistente.get();
            if (Estado.A.equals(meta.getEstado())) {
                throw new DataValidationException("Ya existe una Meta activa con el código especificado.");
            } else {
                // Reactivar registro inactivo
                meta.setEstado(Estado.A);
                meta.setDescripcion(dtoMeta.getDescripcion());
                meta.setCodigoOpnFk(dtoMeta.getCodigoOpnFk());
                meta.setAlineacion(TipoAlineacion.ESTRATEGICA);
                meta.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                meta.setUsuarioCreacion(username);
                return convertToDto(daoMeta.save(meta));
            }
        }

        // Validar que exista el OPN referenciado
        if (!daoOPN.findByCodigo(dtoMeta.getCodigoOpnFk()).isPresent()) {
            throw new DataValidationException("No existe un OPN con el código: " + dtoMeta.getCodigoOpnFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        Meta meta = new Meta();
        meta.setCodigo(dtoMeta.getCodigo());
        meta.setDescripcion(dtoMeta.getDescripcion());
        meta.setEstado(Estado.A); // Nuevo registro siempre activo
        meta.setCodigoOpnFk(dtoMeta.getCodigoOpnFk());
        meta.setAlineacion(TipoAlineacion.ESTRATEGICA);
        meta.setFechaCreacion(new Date());
        meta.setUsuarioCreacion(username);

        return convertToDto(daoMeta.save(meta));
    }

    @Override
    public DtoMeta update(DtoMeta dtoMeta, String accessToken) {
        validateDtoMeta(dtoMeta);
        Meta metaActual = daoMeta.findByCodigo(dtoMeta.getCodigo())
                .orElseThrow(() -> new DataValidationException("La Meta con el código especificado no existe."));

        // Validar que exista el OPN referenciado
        if (!daoOPN.findByCodigo(dtoMeta.getCodigoOpnFk()).isPresent()) {
            throw new DataValidationException("No existe un OPN con el código: " + dtoMeta.getCodigoOpnFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoMeta.getEstado()) && Estado.A.equals(metaActual.getEstado())) {
            throw new DataValidationException("Para desactivar una Meta use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        metaActual.setDescripcion(dtoMeta.getDescripcion());
        metaActual.setEstado(Estado.A);
        metaActual.setCodigoOpnFk(dtoMeta.getCodigoOpnFk());
        metaActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        metaActual.setFechaModificacion(new Date());
        metaActual.setUsuarioModificacion(username);

        validateMeta(metaActual);
        return convertToDto(daoMeta.save(metaActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        Meta meta = daoMeta.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró una Meta con el código especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        meta.setEstado(Estado.I);
        meta.setUsuarioModificacion(username);
        meta.setFechaModificacion(new Date());
        daoMeta.save(meta);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código de la Meta no puede ser nulo o vacío.");
        }
    }

    private void validateDtoMeta(DtoMeta dtoMeta) {
        if (dtoMeta == null) {
            throw new DataValidationException("DtoMeta no puede ser nulo.");
        }
        validateCodigo(dtoMeta.getCodigo());
        if (dtoMeta.getDescripcion() == null || dtoMeta.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoMeta.getCodigoOpnFk() == null || dtoMeta.getCodigoOpnFk().isEmpty()) {
            throw new DataValidationException("Código de OPN es requerido");
        }
    }

    private void validateMeta(Meta meta) {
        if (meta.getDescripcion() == null || meta.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (meta.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo.");
        }
        if (meta.getCodigoOpnFk() == null || meta.getCodigoOpnFk().isEmpty()) {
            throw new DataValidationException("El código de OPN no puede ser nulo o vacío.");
        }
    }

    private DtoMeta convertToDto(Meta meta) {
        if (meta == null) {
            return null;
        }
        DtoMeta dto = new DtoMeta();
        dto.setId(meta.getId());
        dto.setCodigo(meta.getCodigo());
        dto.setDescripcion(meta.getDescripcion());
        dto.setEstado(meta.getEstado());
        dto.setCodigoOpnFk(meta.getCodigoOpnFk());
        dto.setAlineacion(meta.getAlineacion());
        dto.setFechaCreacion(meta.getFechaCreacion());
        dto.setUsuarioCreacion(meta.getUsuarioCreacion());
        dto.setFechaModificacion(meta.getFechaModificacion());
        dto.setUsuarioModificacion(meta.getUsuarioModificacion());
        return dto;
    }
}