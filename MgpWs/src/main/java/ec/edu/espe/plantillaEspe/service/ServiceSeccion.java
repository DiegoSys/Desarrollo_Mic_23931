package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoSeccion;
import ec.edu.espe.plantillaEspe.dao.DaoProyectoSeccion;
import ec.edu.espe.plantillaEspe.dto.DtoSeccion;
import ec.edu.espe.plantillaEspe.dto.DtoSeccionCampo;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Seccion;
import ec.edu.espe.plantillaEspe.model.ProyectoSeccion;
import ec.edu.espe.plantillaEspe.service.IService.IServiceSeccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceSeccion implements IServiceSeccion {    private final DaoSeccion daoSeccion;
    private final DaoProyectoSeccion daoProyectoSeccion;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceSeccion(DaoSeccion daoSeccion, DaoProyectoSeccion daoProyectoSeccion, UserInfoService userInfoService) {
        this.daoSeccion = daoSeccion;
        this.daoProyectoSeccion = daoProyectoSeccion;
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
            throw new RuntimeException("Error al obtener todas las secciones.", e);
        }
    }

    @Override
    public List<DtoSeccion> findAllActivos() {
        try {
            return daoSeccion.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las secciones activas.", e);
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
    public Page<DtoSeccion> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoSeccion.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } else {
            List<DtoSeccion> activos = daoSeccion.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(activos, searchCriteria, pageable);
        }
    }

    @Override
    @Transactional
    public DtoSeccion save(DtoSeccion dtoSeccion, String accessToken) {
        validateDtoSeccion(dtoSeccion);

        Optional<Seccion> existente = daoSeccion.findByCodigo(dtoSeccion.getCodigo());
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        if (existente.isPresent()) {
            Seccion seccion = existente.get();
            if (Estado.A.equals(seccion.getEstado())) {
                throw new DataValidationException("Ya existe una sección activa con el código especificado.");
            }
            // Reactivar registro inactivo
            seccion.setEstado(Estado.A);
            seccion.setNombre(dtoSeccion.getNombre());
            seccion.setDescripcion(dtoSeccion.getDescripcion());
            seccion.setFechaCreacion(new Date());
            seccion.setUsuarioCreacion(username);
            return convertToDto(daoSeccion.save(seccion));
        }

        Seccion seccion = new Seccion();
        seccion.setCodigo(dtoSeccion.getCodigo());
        seccion.setNombre(dtoSeccion.getNombre());
        seccion.setDescripcion(dtoSeccion.getDescripcion());
        seccion.setEstado(Estado.A);
        seccion.setFechaCreacion(new Date());
        seccion.setUsuarioCreacion(username);

        return convertToDto(daoSeccion.save(seccion));
    }

    @Override
    @Transactional
    public DtoSeccion update(DtoSeccion dtoSeccion, String accessToken) {
        validateDtoSeccion(dtoSeccion);

        Seccion seccionActual = daoSeccion.findByCodigo(dtoSeccion.getCodigo())
                .orElseThrow(() -> new DataValidationException("La sección con el código especificado no existe."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoSeccion.getEstado()) && Estado.A.equals(seccionActual.getEstado())) {
            throw new DataValidationException("Para desactivar una sección use el método delete()");
        }

        seccionActual.setDescripcion(dtoSeccion.getDescripcion());
        seccionActual.setNombre(dtoSeccion.getNombre());
        seccionActual.setEstado(Estado.A);
        seccionActual.setFechaModificacion(new Date());
        seccionActual.setUsuarioModificacion(username);

        return convertToDto(daoSeccion.save(seccionActual));
    }    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        Seccion seccion = daoSeccion.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró una sección con el código especificado."));

        // Verificar si la sección tiene relaciones activas con proyectos
        List<ProyectoSeccion> relacionesActivas = daoProyectoSeccion.findBySeccionCodigo(codigo);
        if (!relacionesActivas.isEmpty()) {
            // Opción 1: Lanzar excepción (más estricto)
            throw new DataValidationException("No se puede eliminar la sección porque tiene " + 
                relacionesActivas.size() + " relaciones activas con proyectos. " +
                "Primero debe eliminar las relaciones en los proyectos asociados.");
            
            // Opción 2: Eliminar automáticamente las relaciones (comentado)
            /*
            for (ProyectoSeccion proyectoSeccion : relacionesActivas) {
                daoProyectoSeccion.delete(proyectoSeccion);
            }
            */
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        seccion.setEstado(Estado.I);
        seccion.setFechaModificacion(new Date());
        seccion.setUsuarioModificacion(username);
        daoSeccion.save(seccion);
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
        if (dtoSeccion.getNombre() == null || dtoSeccion.getNombre().isEmpty()) {
            throw new DataValidationException("Nombre es requerido.");
        }
        if (dtoSeccion.getDescripcion() == null || dtoSeccion.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida");
        }
    }

    private DtoSeccion convertToDto(Seccion seccion) {
        if (seccion == null) {
            return null;
        }
        DtoSeccion dto = new DtoSeccion();
        dto.setId(seccion.getId());
        dto.setCodigo(seccion.getCodigo());
        dto.setNombre(seccion.getNombre());
        dto.setDescripcion(seccion.getDescripcion());
        dto.setEstado(seccion.getEstado());
        dto.setFechaCreacion(seccion.getFechaCreacion());
        dto.setUsuarioCreacion(seccion.getUsuarioCreacion());
        dto.setFechaModificacion(seccion.getFechaModificacion());
        dto.setUsuarioModificacion(seccion.getUsuarioModificacion());

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