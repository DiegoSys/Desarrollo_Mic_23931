package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoSeccion;
import ec.edu.espe.plantillaEspe.dto.DtoSeccion;
import ec.edu.espe.plantillaEspe.dto.DtoSeccionCampo;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Seccion;
import ec.edu.espe.plantillaEspe.service.IService.IServiceSeccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceSeccion implements IServiceSeccion {

    private final DaoSeccion daoSeccion;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceSeccion(DaoSeccion daoSeccion,
                          UserInfoService userInfoService) {
        this.daoSeccion = daoSeccion;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoSeccion find(String codigo) {
        validateCodigo(codigo);
        return daoSeccion.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró una sección con el código especificado."));
    }

    @Override
    public List<DtoSeccion> findAll() {
        try {
            return daoSeccion.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los secciones.", e);
        }
    }

    @Override
    public Page<DtoSeccion> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoSeccion.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las secciones paginadas.", e);
        }
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        validateCodigo(codigo);
        Seccion seccion = daoSeccion.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró una sección con el código especificado."));

        try {
            daoSeccion.delete(seccion);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la sección.", e);
        }
    }

    @Override
    public DtoSeccion save(DtoSeccion dtoSeccion, String accessToken) {
        validateDtoSeccion(dtoSeccion);

        if (daoSeccion.findByCodigo(dtoSeccion.getCodigo()).isPresent()) {
            throw new DataValidationException("Ya existe una sección con el código especificado.");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        Seccion seccion = new Seccion();
        seccion.setCodigo(dtoSeccion.getCodigo());
        seccion.setDescripcion(dtoSeccion.getDescripcion());
        seccion.setEstado("1");
        seccion.setCreationDateA(new Date());
        seccion.setCreationUser(username);

        // Primero guardamos la sección
        Seccion seccionGuardada = daoSeccion.save(seccion);

        // Ya no manejamos SeccionCampo aquí, se manejará a través de su propio servicio

        return convertToDto(seccionGuardada);
    }

    @Override
    public DtoSeccion update(DtoSeccion dtoSeccion, String accessToken) {
        validateDtoSeccion(dtoSeccion);
        validateSeccion(dtoSeccion);

        Seccion seccionActual = daoSeccion.findByCodigo(dtoSeccion.getCodigo())
                .orElseThrow(() -> new DataValidationException("La sección con el código especificado no existe."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        seccionActual.setDescripcion(dtoSeccion.getDescripcion());
        seccionActual.setEstado(dtoSeccion.getEstado());
        seccionActual.setModificationDate(new Date());
        seccionActual.setModificationUser(username);

        return convertToDto(daoSeccion.save(seccionActual));
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código de la sección no puede ser nulo o vacío.");
        }
    }

    private void validateDtoSeccion(DtoSeccion dtoSeccion) {
        if (dtoSeccion == null) {
            throw new DataValidationException("DtoSeccion no puede ser nulo");
        }
        validateCodigo(dtoSeccion.getCodigo());
        if (dtoSeccion.getDescripcion() == null || dtoSeccion.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida");
        }
    }

    private void validateSeccion(DtoSeccion dtoSeccion) {
        if (dtoSeccion.getEstado() != null && !List.of("0", "1").contains(dtoSeccion.getEstado())) {
            throw new DataValidationException("Estado debe ser '0' o '1'");
        }
    }

    private DtoSeccion convertToDto(Seccion seccion) {
        if (seccion == null) {
            return null;
        }
        DtoSeccion dto = new DtoSeccion();
        dto.setId(seccion.getId());
        dto.setCodigo(seccion.getCodigo());
        dto.setDescripcion(seccion.getDescripcion());
        dto.setEstado(seccion.getEstado());
        dto.setFechaCreacion(seccion.getCreationDateA());
        dto.setUsuarioCreacion(seccion.getCreationUser());
        dto.setFechaModificacion(seccion.getModificationDate());
        dto.setUsuarioModificacion(seccion.getModificationUser());

        // Convertir los proyectos
        if (seccion.getSeccionCampos() != null) {
            List<DtoSeccionCampo> campos = seccion.getSeccionCampos().stream()
                    .map(ps -> {
                        DtoSeccionCampo dtoSeccionCampo = new DtoSeccionCampo();
                        dtoSeccionCampo.setId(ps.getId());
                        dtoSeccionCampo.setCodigo(ps.getCodigo());
                        dtoSeccionCampo.setFechaCreacion(ps.getFechaCreacion());
                        dtoSeccionCampo.setUsuarioCreacion(ps.getUsuarioCreacion());
                        dtoSeccionCampo.setFechaModificacion(ps.getFechaModificacion());
                        dtoSeccionCampo.setUsuarioModificacion(ps.getUsuarioModificacion());
                        dtoSeccionCampo.setCodigoCampoFk(ps.getCampo().getCodigo());
                        dtoSeccionCampo.setCodigoSeccionFk(ps.getSeccion().getCodigo());
                        return dtoSeccionCampo;
                    })
                    .collect(Collectors.toList());
            dto.setCampos(campos);
        }

        return dto;
    }
}