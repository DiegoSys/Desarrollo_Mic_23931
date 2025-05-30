package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoCampo;
import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Campo;
import ec.edu.espe.plantillaEspe.service.IService.IServiceCampo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ServiceCampo implements IServiceCampo {

    private final DaoCampo daoCampo;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceCampo(DaoCampo daoCampo, UserInfoService userInfoService) {
        this.daoCampo = daoCampo;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoCampo find(String codigo) {
        validateCodigo(codigo);
        return daoCampo.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un campo con el código especificado."));
    }

    @Override
    public List<DtoCampo> findAll() {
        try {
            return daoCampo.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los campos.", e);
        }
    }

    @Override
    public Page<DtoCampo> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoCampo.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los campos paginados.", e);
        }
    }

    @Override
    public DtoCampo save(DtoCampo dtoCampo, String accessToken) {
        validateDtoCampo(dtoCampo);

        if (daoCampo.findByCodigo(dtoCampo.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe un campo con el código especificado.");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        Campo campo = new Campo();
        campo.setCodigo(dtoCampo.getCodigo());
        campo.setDescripcion(dtoCampo.getDescripcion());
        campo.setEstado(dtoCampo.getEstado() != null ? dtoCampo.getEstado() : "1");
        campo.setFechaCreacion(new Date());
        campo.setUsuarioCreacion(username);

        return convertToDto(daoCampo.save(campo));
    }

    @Override
    public DtoCampo update(DtoCampo dtoCampo, String accessToken) {
        validateDtoCampo(dtoCampo);
        Campo campoActual = daoCampo.findByCodigo(dtoCampo.getCodigo())
                .orElseThrow(() -> new DataValidationException("El campo con el código especificado no existe."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        campoActual.setDescripcion(dtoCampo.getDescripcion());
        if (dtoCampo.getEstado() != null) {
            campoActual.setEstado(dtoCampo.getEstado());
        }
        campoActual.setFechaModificacion(new Date());
        campoActual.setUsuarioModificacion(username);

        validateCampo(campoActual);
        return convertToDto(daoCampo.save(campoActual));
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        validateCodigo(codigo);
        Campo campo = daoCampo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un campo con el código especificado."));

        try {
            daoCampo.delete(campo);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el campo.", e);
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del campo no puede ser nulo o vacío.");
        }
    }

    private void validateDtoCampo(DtoCampo dtoCampo) {
        if (dtoCampo == null) {
            throw new DataValidationException("DtoCampo no puede ser nulo");
        }
        validateCodigo(dtoCampo.getCodigo());
        if (dtoCampo.getDescripcion() == null || dtoCampo.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida");
        }
        if (dtoCampo.getEstado() != null && !List.of("0", "1").contains(dtoCampo.getEstado())) {
            throw new DataValidationException("Estado debe ser '0' o '1'");
        }
    }

    private void validateCampo(Campo campo) {
        if (campo.getDescripcion() == null || campo.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción del campo no puede ser nula o vacía.");
        }
        if (campo.getEstado() == null || (!campo.getEstado().equals("1") && !campo.getEstado().equals("0"))) {
            throw new DataValidationException("El estado del campo debe ser '1' (activo) o '0' (inactivo).");
        }
    }

    private DtoCampo convertToDto(Campo campo) {
        if (campo == null) {
            return null;
        }
        DtoCampo dto = new DtoCampo();
        dto.setId(campo.getId());
        dto.setCodigo(campo.getCodigo());
        dto.setDescripcion(campo.getDescripcion());
        dto.setEstado(campo.getEstado());
        dto.setFechaCreacion(campo.getFechaCreacion());
        dto.setUsuarioCreacion(campo.getUsuarioCreacion());
        dto.setFechaModificacion(campo.getFechaModificacion());
        dto.setUsuarioModificacion(campo.getUsuarioModificacion());
        return dto;
    }
}