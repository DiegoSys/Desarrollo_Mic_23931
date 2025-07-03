package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoPresGrupo;
import ec.edu.espe.plantillaEspe.dao.DaoPresItem;
import ec.edu.espe.plantillaEspe.dao.DaoPresNaturaleza;
import ec.edu.espe.plantillaEspe.dao.DaoPresSubgrupo;
import ec.edu.espe.plantillaEspe.dto.DtoPresItem;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.PresItem;
import ec.edu.espe.plantillaEspe.model.PresSubgrupo;
import ec.edu.espe.plantillaEspe.service.IService.IServicePresItem;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ServicePresItem implements IServicePresItem {
    private final DaoPresItem daoPresItem;
    private final UserInfoService userInfoService;
    private final DaoPresSubgrupo daoPresSubgrupo;

    @Autowired
    public ServicePresItem(DaoPresItem daoPresItem,
                           UserInfoService userInfoService,
                           DaoPresSubgrupo daoPresSubgrupo) {
        this.daoPresItem = daoPresItem;
        this.userInfoService = userInfoService;
        this.daoPresSubgrupo = daoPresSubgrupo;
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
    public Page<DtoPresItem> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            try {
                return daoPresItem.findByEstado(Estado.A, pageable).map(this::convertToDto);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener los items activos paginados.", e);
            }
        } else {
            try {
                List<DtoPresItem> items = daoPresItem.findByEstado(Estado.A).stream()
                        .map(this::convertToDto)
                        .toList();
                return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(items, searchCriteria, pageable);
            } catch (Exception e) {
                throw new RuntimeException("Error al filtrar y paginar los items activos.", e);
            }
        }
    }

    @Override
    public Page<DtoPresItem> findByPresSubgrupo_Codigo(String codigo, Pageable pageable, Map<String, String> searchCriteria) {
        validateCodigo(codigo);
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoPresItem.findByPresSubgrupo_CodigoAndEstado(codigo, Estado.A, pageable)
                    .map(this::convertToDto);
        } else {
            List<DtoPresItem> items = daoPresItem.findByPresSubgrupo_CodigoAndEstado(codigo, Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
            return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(items, searchCriteria, pageable);
        }
    }

    @Override
    @Transactional
    public DtoPresItem save(DtoPresItem dtoPresItem, String accessToken) {
        validateDtoPresItem(dtoPresItem);

        Optional<PresItem> existente = daoPresItem.findByCodigo(dtoPresItem.getCodigo());
        String username = getUsername(accessToken);

        PresItem presItem;
        if (existente.isPresent()) {
            presItem = existente.get();
            if (Estado.A.equals(presItem.getEstado())) {
                throw new DataValidationException("Ya existe un item activo con el código especificado.");
            }
            presItem.setEstado(Estado.A);
            presItem.setAlineacion(TipoAlineacion.OPERATIVA);
            presItem.setDescripcion(dtoPresItem.getDescripcion());
            presItem.setNombre(dtoPresItem.getNombre());
            presItem.setFechaCreacion(new Date());
            presItem.setUsuarioCreacion(username);
        } else {
            presItem = new PresItem();
            presItem.setCodigo(dtoPresItem.getCodigo());
            presItem.setFechaCreacion(new Date());
            presItem.setUsuarioCreacion(username);
            presItem.setEstado(Estado.A);
            presItem.setAlineacion(TipoAlineacion.OPERATIVA);
            presItem.setDescripcion(dtoPresItem.getDescripcion());
            presItem.setNombre(dtoPresItem.getNombre());
        }

        // Relación padre: Subgrupo
        if (dtoPresItem.getCodigoSubGrupoFk() != null) {
            PresSubgrupo subgrupo = daoPresSubgrupo.findByCodigo(dtoPresItem.getCodigoSubGrupoFk())
                    .orElseThrow(() -> new DataValidationException("No se encontró el subgrupo padre especificado."));
            presItem.setPresSubgrupo(subgrupo);
        } else {
            presItem.setPresSubgrupo(null);
        }

        // Generar y almacenar el código compuesto
        //presItem.setCodigoCompuesto(generarCodigoCompuesto(presItem));

        return convertToDto(daoPresItem.save(presItem));
    }

    @Override
    @Transactional
    public DtoPresItem update(DtoPresItem dtoPresItem, String accessToken) {
        validateDtoPresItem(dtoPresItem);

        PresItem presItemActual = daoPresItem.findByCodigo(dtoPresItem.getCodigo())
                .orElseThrow(() -> new DataValidationException("El item con el código especificado no existe."));

        if (Estado.I.equals(dtoPresItem.getEstado()) && Estado.A.equals(presItemActual.getEstado())) {
            throw new DataValidationException("Para desactivar un item use el método delete()");
        }

        String username = getUsername(accessToken);
        presItemActual.setFechaModificacion(new Date());
        presItemActual.setUsuarioModificacion(username);
        presItemActual.setEstado(Estado.A);
        presItemActual.setAlineacion(TipoAlineacion.OPERATIVA);
        presItemActual.setDescripcion(dtoPresItem.getDescripcion());
        presItemActual.setNombre(dtoPresItem.getNombre());

        // Relación padre: Subgrupo
        if (dtoPresItem.getCodigoSubGrupoFk() != null) {
            PresSubgrupo subgrupo = daoPresSubgrupo.findByCodigo(dtoPresItem.getCodigoSubGrupoFk())
                    .orElseThrow(() -> new DataValidationException("No se encontró el subgrupo padre especificado."));
            presItemActual.setPresSubgrupo(subgrupo);
        } else {
            presItemActual.setPresSubgrupo(null);
        }

        // Generar y almacenar el código compuesto
        //presItemActual.setCodigoCompuesto(generarCodigoCompuesto(presItemActual));

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
        presItem.setPresSubgrupo(null); // Desvincular subgrupo padre
        //presItem.setCodigoCompuesto(null); // Eliminar el código compuesto
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

    /**
     * Genera el código compuesto y lo almacena en el item.
     * Formato: codigoNaturaleza.codigoGrupo.codigoSubgrupo.codigoItem
     */
    private String generarCodigoCompuesto(PresItem item) {
        String codigoNaturaleza = "0";
        String codigoGrupo = "0";
        String codigoSubgrupo = "0";
        String codigoItem = item.getCodigo() != null ? item.getCodigo() : "0";

        if (item.getPresSubgrupo() != null) {
            codigoSubgrupo = item.getPresSubgrupo().getCodigo() != null ? item.getPresSubgrupo().getCodigo() : "0";
            if (item.getPresSubgrupo().getPresGrupo() != null) {
                codigoGrupo = item.getPresSubgrupo().getPresGrupo().getCodigo() != null ? item.getPresSubgrupo().getPresGrupo().getCodigo() : "0";
                if (item.getPresSubgrupo().getPresGrupo().getPresNaturaleza() != null) {
                    codigoNaturaleza = item.getPresSubgrupo().getPresGrupo().getPresNaturaleza().getCodigo() != null ? item.getPresSubgrupo().getPresGrupo().getPresNaturaleza().getCodigo() : "0";
                }
            }
        }
        return String.format("%s.%s.%s.%s", codigoNaturaleza, codigoGrupo, codigoSubgrupo, codigoItem);
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
        dto.setAlineacion(presItem.getAlineacion());
        dto.setDescripcion(presItem.getDescripcion());
        dto.setNombre(presItem.getNombre());

        // Relación padre: subgrupo
        if (presItem.getPresSubgrupo() != null) {
            dto.setCodigoSubGrupoFk(presItem.getPresSubgrupo().getCodigo());
        }

        // Código compuesto almacenado
        //dto.setCodigoCompuesto(presItem.getCodigoCompuesto());

        return dto;
    }
}