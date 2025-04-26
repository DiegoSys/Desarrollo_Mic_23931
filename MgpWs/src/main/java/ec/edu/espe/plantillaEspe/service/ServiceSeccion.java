package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoCampo;
import ec.edu.espe.plantillaEspe.dao.DaoSeccion;
import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import ec.edu.espe.plantillaEspe.dto.DtoSeccion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Campo;
import ec.edu.espe.plantillaEspe.model.Seccion;
import ec.edu.espe.plantillaEspe.model.SeccionCampo;
import ec.edu.espe.plantillaEspe.service.IService.IServiceSeccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ServiceSeccion implements IServiceSeccion {

    private final DaoSeccion daoSeccion;
    private final UserInfoService userInfoService;
    private final DaoCampo daoCampo;

    @Autowired
    public ServiceSeccion(DaoSeccion daoSeccion, UserInfoService userInfoService, DaoCampo daoCampo) {
        this.daoSeccion = daoSeccion;
        this.userInfoService = userInfoService;
        this.daoCampo = daoCampo;
    }

    @Override
    public DtoSeccion find(String codigo) {
        validateCodigo(codigo);
        Seccion seccion = daoSeccion.findById(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró una sección con el código especificado."));
        return convertToDto(seccion);
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
    public DtoSeccion save(DtoSeccion dtoSeccion, String accessToken) {
        validateDtoSeccion(dtoSeccion);

        // Verificar si ya existe una sección con el mismo código
        if (daoSeccion.existsById(dtoSeccion.getCodigo())) {
            throw new DataValidationException("Ya existe una sección con el código especificado.");
        }

        // Obtener información del usuario
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("username");

        // Convertir DTO a Entidad
        Seccion seccion = new Seccion();
        seccion.setCodigo(dtoSeccion.getCodigo());
        seccion.setDescripcion(dtoSeccion.getDescripcion());
        seccion.setEstado("1"); // 1 = ACTIVO
        seccion.setCreationDateA(new Date());
        seccion.setCreationUser(username);

        // Procesar los campos asociados a la sección
        if (dtoSeccion.getCampos() != null && !dtoSeccion.getCampos().isEmpty()) {
            Set<SeccionCampo> seccionCampos = new HashSet<>();

            for (DtoCampo dtoCampo : dtoSeccion.getCampos()) {
                SeccionCampo seccionCampo = new SeccionCampo();
                seccionCampo.setSeccion(seccion);

                // Buscar el campo en la base de datos o crear uno nuevo
                Campo campo = daoCampo.findById(dtoCampo.getCodigo())
                        .orElseGet(() -> {
                            Campo nuevoCampo = new Campo();
                            nuevoCampo.setCodigo(dtoCampo.getCodigo());
                            nuevoCampo.setDescripcion(dtoCampo.getDescripcion());
                            nuevoCampo.setEstado("1");
                            nuevoCampo.setUsuarioCreacion(username);
                            nuevoCampo.setFechaCreacion(new Date());

                            return daoCampo.save(nuevoCampo);
                        });

                seccionCampo.setCampo(campo);
                campo.getSeccionCampos().add(seccionCampo);
                seccionCampos.add(seccionCampo);
            }

            seccion.setSeccionCampos(seccionCampos);
        }

        // Guardar la sección
        Seccion seccionGuardada = daoSeccion.save(seccion);

        // Convertir la entidad guardada de vuelta a DTO
        return convertToDto(seccionGuardada);
    }

    @Override
    public DtoSeccion update(DtoSeccion dtoSeccion, String accessToken) {
        validateDtoSeccion(dtoSeccion);
        Seccion seccionActual = daoSeccion.findById(dtoSeccion.getCodigo())
                .orElseThrow(() -> new DataValidationException("La sección con el código especificado no existe."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);

        updateEntityFromDto(seccionActual, dtoSeccion);
        validateSeccion(seccionActual);
        seccionActual.setModificationDate(new Date());
        seccionActual.setModificationUser((String) userInfo.get("name"));

        return convertToDto(daoSeccion.save(seccionActual));
    }

    @Override
    public void delete(String codigo) {
        validateCodigo(codigo);
        if (!daoSeccion.existsById(codigo)) {
            throw new DataValidationException("No se encontró una sección con el código especificado.");
        }
        try {
            daoSeccion.deleteById(codigo);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la sección con el código especificado.", e);
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código de la sección no puede ser nulo o vacío.");
        }
    }

    private void validateDtoSeccion(DtoSeccion dtoSeccion) {
        if (dtoSeccion == null) {
            throw new DataValidationException("El objeto DtoSeccion no puede ser nulo.");
        }
        validateCodigo(dtoSeccion.getCodigo());
        if (dtoSeccion.getDescripcion() == null || dtoSeccion.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción de la sección no puede ser nula o vacía.");
        }
        if (dtoSeccion.getEstado() != null && (!dtoSeccion.getEstado().equals("1") && !dtoSeccion.getEstado().equals("0"))) {
            throw new DataValidationException("El estado de la sección debe ser '1' (activo) o '0' (inactivo) si se proporciona.");
        }
    }

    private void validateSeccion(Seccion seccion) {
        if (seccion.getDescripcion() == null || seccion.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción de la sección no puede ser nula o vacía.");
        }
        if (seccion.getEstado() == null || (!seccion.getEstado().equals("1") && !seccion.getEstado().equals("0"))) {
            throw new DataValidationException("El estado de la sección debe ser '1' (activo) o '0' (inactivo).");
        }
    }

    private DtoSeccion convertToDto(Seccion seccion) {
        DtoSeccion dto = new DtoSeccion();
        dto.setCodigo(seccion.getCodigo());
        dto.setDescripcion(seccion.getDescripcion());
        dto.setEstado(seccion.getEstado());
        dto.setFechaCreacion(seccion.getCreationDateA() );
        dto.setUsuarioCreacion(seccion.getCreationUser());
        dto.setFechaModificacion(seccion.getModificationDate());
        dto.setUsuarioModificacion(seccion.getModificationUser());
        return dto;
    }

    private Seccion convertToEntity(DtoSeccion dto) {
        Seccion seccion = new Seccion();
        seccion.setCodigo(dto.getCodigo());
        seccion.setDescripcion(dto.getDescripcion());
        seccion.setEstado(dto.getEstado());
        seccion.setCreationDateA(dto.getFechaCreacion());
        seccion.setCreationUser(dto.getUsuarioCreacion());
        seccion.setModificationDate(dto.getFechaModificacion());
        seccion.setModificationUser(dto.getUsuarioModificacion());
        return seccion;
    }

    private void updateEntityFromDto(Seccion seccion, DtoSeccion dto) {
        if (dto.getDescripcion() != null) {
            seccion.setDescripcion(dto.getDescripcion());
        }
        if (dto.getEstado() != null) {
            seccion.setEstado(dto.getEstado());
        }
    }
}
