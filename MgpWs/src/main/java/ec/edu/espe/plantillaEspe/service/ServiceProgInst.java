package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoProgInst;
import ec.edu.espe.plantillaEspe.dao.DaoMeta;
import ec.edu.espe.plantillaEspe.dto.DtoProgInst;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.ProgInst;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProgInst;
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
public class ServiceProgInst implements IServiceProgInst {

    private final DaoProgInst daoProgInst;
    private final UserInfoService userInfoService;
    private final DaoMeta daoMeta;

    @Autowired
    public ServiceProgInst(DaoProgInst daoProgInst,
                           UserInfoService userInfoService,
                           DaoMeta daoMeta) {
        this.daoProgInst = daoProgInst;
        this.userInfoService = userInfoService;
        this.daoMeta = daoMeta;
    }

    @Override
    public DtoProgInst find(String codigo) {
        validateCodigo(codigo);
        return daoProgInst.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un programa institucional con el código especificado."));
    }

    @Override
    public List<DtoProgInst> findAll() {
        try {
            return daoProgInst.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los programas institucionales.", e);
        }
    }

    @Override
    public List<DtoProgInst> findAllActivos() {
        try {
            return daoProgInst.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los programas institucionales activos.", e);
        }
    }

    @Override
    public Page<DtoProgInst> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoProgInst.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los programas institucionales paginados.", e);
        }
    }

    @Override
    public Page<DtoProgInst> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoProgInst.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los programas institucionales paginados.", e);
        }
    }

    @Override
    public DtoProgInst save(DtoProgInst dtoProgInst, String accessToken) {
        validateDtoProgInst(dtoProgInst);

        Optional<ProgInst> progInstExistente = daoProgInst.findByCodigo(dtoProgInst.getCodigo());
        if (progInstExistente.isPresent()) {
            ProgInst progInst = progInstExistente.get();
            if (Estado.A.equals(progInst.getEstado())) {
                throw new DataValidationException("Ya existe un programa institucional activo con el código especificado.");
            } else {
                // Reactivar registro inactivo
                progInst.setEstado(Estado.A);
                progInst.setDescripcion(dtoProgInst.getDescripcion());
                progInst.setAlineacion(TipoAlineacion.ESTRATEGICA);
                progInst.setCodigoMetaFk(dtoProgInst.getCodigoMetaFk());
                progInst.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                progInst.setUsuarioCreacion(username);
                return convertToDto(daoProgInst.save(progInst));
            }
        }

        // Validar que exista la Meta referenciada
        if (!daoMeta.findByCodigo(dtoProgInst.getCodigoMetaFk()).isPresent()) {
            throw new DataValidationException("No existe una meta con el código: " + dtoProgInst.getCodigoMetaFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        ProgInst progInst = new ProgInst();
        progInst.setCodigo(dtoProgInst.getCodigo());
        progInst.setDescripcion(dtoProgInst.getDescripcion());
        progInst.setEstado(Estado.A); // Nuevo registro siempre activo
        progInst.setAlineacion(TipoAlineacion.ESTRATEGICA);
        progInst.setCodigoMetaFk(dtoProgInst.getCodigoMetaFk());
        progInst.setFechaCreacion(new Date());
        progInst.setUsuarioCreacion(username);

        return convertToDto(daoProgInst.save(progInst));
    }

    @Override
    public DtoProgInst update(DtoProgInst dtoProgInst, String accessToken) {
        validateDtoProgInst(dtoProgInst);
        ProgInst progInstActual = daoProgInst.findByCodigo(dtoProgInst.getCodigo())
                .orElseThrow(() -> new DataValidationException("El programa institucional con el código especificado no existe."));

        // Validar que exista la Meta referenciada
        if (!daoMeta.findByCodigo(dtoProgInst.getCodigoMetaFk()).isPresent()) {
            throw new DataValidationException("No existe una meta con el código: " + dtoProgInst.getCodigoMetaFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoProgInst.getEstado()) && Estado.A.equals(progInstActual.getEstado())) {
            throw new DataValidationException("Para desactivar un programa institucional use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        progInstActual.setDescripcion(dtoProgInst.getDescripcion());
        progInstActual.setEstado(Estado.A);
        progInstActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        progInstActual.setCodigoMetaFk(dtoProgInst.getCodigoMetaFk());
        progInstActual.setFechaModificacion(new Date());
        progInstActual.setUsuarioModificacion(username);

        validateProgInst(progInstActual);
        return convertToDto(daoProgInst.save(progInstActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        ProgInst progInst = daoProgInst.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un programa institucional con el código especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        progInst.setUsuarioModificacion(username);
        progInst.setFechaModificacion(new Date());
        progInst.setEstado(Estado.I);
        daoProgInst.save(progInst);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del programa institucional no puede ser nulo o vacío.");
        }
    }

    private void validateDtoProgInst(DtoProgInst dtoProgInst) {
        if (dtoProgInst == null) {
            throw new DataValidationException("DtoProgInst no puede ser nulo.");
        }
        validateCodigo(dtoProgInst.getCodigo());
        if (dtoProgInst.getDescripcion() == null || dtoProgInst.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoProgInst.getCodigoMetaFk() == null || dtoProgInst.getCodigoMetaFk().isEmpty()) {
            throw new DataValidationException("Código de Meta es requerido.");
        }
    }

    private void validateProgInst(ProgInst progInst) {
        if (progInst.getDescripcion() == null || progInst.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (progInst.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo o vacío.");
        }
        if (progInst.getCodigoMetaFk() == null || progInst.getCodigoMetaFk().isEmpty()) {
            throw new DataValidationException("El código de Meta no puede ser nulo o vacío.");
        }
    }

    private DtoProgInst convertToDto(ProgInst progInst) {
        if (progInst == null) {
            return null;
        }
        DtoProgInst dto = new DtoProgInst();
        dto.setId(progInst.getId());
        dto.setCodigo(progInst.getCodigo());
        dto.setDescripcion(progInst.getDescripcion());
        dto.setAlineacion(progInst.getAlineacion());
        dto.setEstado(progInst.getEstado());
        dto.setCodigoMetaFk(progInst.getCodigoMetaFk());
        dto.setFechaCreacion(progInst.getFechaCreacion());
        dto.setUsuarioCreacion(progInst.getUsuarioCreacion());
        dto.setFechaModificacion(progInst.getFechaModificacion());
        dto.setUsuarioModificacion(progInst.getUsuarioModificacion());
        return dto;
    }
}