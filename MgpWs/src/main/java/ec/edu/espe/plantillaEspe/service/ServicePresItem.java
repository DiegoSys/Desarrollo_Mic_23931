package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoPresItem;
import ec.edu.espe.plantillaEspe.dto.DtoPresItem;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.PresItem;
import ec.edu.espe.plantillaEspe.service.IService.IServicePresItem;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServicePresItem implements IServicePresItem {

    private final DaoPresItem daoPresItem;
    private final UserInfoService userInfoService;

    @Autowired
    public ServicePresItem(DaoPresItem daoPresItem,
                           UserInfoService userInfoService) {
        this.daoPresItem = daoPresItem;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoPresItem find(String codigo) {
        validateCodigo(codigo);
        return daoPresItem.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un item con el código especificado."));
    }

    @Override
    public List<DtoPresItem> findAll() {
        try {
            return daoPresItem.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los items.", e);
        }
    }

    @Override
    public List<DtoPresItem> findAllActivos() {
        try {
            return daoPresItem.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los items activos.", e);
        }
    }

    @Override
    public Page<DtoPresItem> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPresItem.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los items paginados.", e);
        }
    }

    @Override
    public Page<DtoPresItem> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPresItem.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los items activos paginados.", e);
        }
    }

    @Override
    @Transactional
    public DtoPresItem save(DtoPresItem dtoPresItem, String accessToken) {
        validateDtoPresItem(dtoPresItem);

        Optional<PresItem> existente = daoPresItem.findByCodigo(dtoPresItem.getCodigo());
        if (existente.isPresent()) {
            PresItem item = existente.get();
            if (Estado.A.equals(item.getEstado())) {
                throw new DataValidationException("Ya existe un item activo con el código especificado.");
            }
            // Reactivar registro inactivo
            String username = getUsername(accessToken);
            item.setEstado(Estado.A);
            item.setDescripcion(dtoPresItem.getDescripcion());
            item.setNombre(dtoPresItem.getNombre());
            item.setFechaCreacion(new Date());
            item.setUsuarioCreacion(username);
            return convertToDto(daoPresItem.save(item));
        }

        String username = getUsername(accessToken);
        PresItem presItem = new PresItem();
        presItem.setCodigo(dtoPresItem.getCodigo());
        presItem.setFechaCreacion(new Date());
        presItem.setUsuarioCreacion(username);
        presItem.setEstado(Estado.A);
        presItem.setDescripcion(dtoPresItem.getDescripcion());
        presItem.setNombre(dtoPresItem.getNombre());

        return convertToDto(daoPresItem.save(presItem));
    }

    @Override
    @Transactional
    public DtoPresItem update(DtoPresItem dtoPresItem, String accessToken) {
        validateDtoPresItem(dtoPresItem);

        PresItem presItemActual = daoPresItem.findByCodigo(dtoPresItem.getCodigo())
                .orElseThrow(() -> new DataValidationException("El item con el código especificado no existe."));

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoPresItem.getEstado()) && Estado.A.equals(presItemActual.getEstado())) {
            throw new DataValidationException("Para desactivar un item use el método delete()");
        }

        String username = getUsername(accessToken);
        presItemActual.setFechaModificacion(new Date());
        presItemActual.setUsuarioModificacion(username);
        presItemActual.setEstado(dtoPresItem.getEstado());
        presItemActual.setDescripcion(dtoPresItem.getDescripcion());
        presItemActual.setNombre(dtoPresItem.getNombre());

        return convertToDto(daoPresItem.save(presItemActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        PresItem presItem = daoPresItem.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un item con el código especificado."));

        String username = getUsername(accessToken);
        presItem.setEstado(Estado.I);
        presItem.setFechaModificacion(new Date());
        presItem.setUsuarioModificacion(username);
        daoPresItem.save(presItem);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del item no puede ser nulo o vacío.");
        }
    }

    private void validateDtoPresItem(DtoPresItem dtoPresItem) {
        if (dtoPresItem == null) {
            throw new DataValidationException("DtoPresItem no puede ser nulo.");
        }
        validateCodigo(dtoPresItem.getCodigo());
        if (dtoPresItem.getDescripcion() == null || dtoPresItem.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción es requerida.");
        }
        if (dtoPresItem.getNombre() == null || dtoPresItem.getNombre().isEmpty()) {
            throw new DataValidationException("El nombre es requerido.");
        }
    }

    private String getUsername(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private DtoPresItem convertToDto(PresItem presItem) {
        if (presItem == null) {
            return null;
        }
        DtoPresItem dto = new DtoPresItem();
        dto.setId(presItem.getId());
        dto.setCodigo(presItem.getCodigo());
        dto.setFechaCreacion(presItem.getFechaCreacion());
        dto.setUsuarioCreacion(presItem.getUsuarioCreacion());
        dto.setFechaModificacion(presItem.getFechaModificacion());
        dto.setUsuarioModificacion(presItem.getUsuarioModificacion());
        dto.setEstado(presItem.getEstado());
        dto.setDescripcion(presItem.getDescripcion());
        dto.setNombre(presItem.getNombre());

        return dto;
    }
}