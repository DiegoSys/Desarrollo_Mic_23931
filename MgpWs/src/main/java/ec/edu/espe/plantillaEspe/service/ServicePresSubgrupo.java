package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoPresGrupo;
import ec.edu.espe.plantillaEspe.dao.DaoPresItem;
import ec.edu.espe.plantillaEspe.dao.DaoPresSubgrupo;
import ec.edu.espe.plantillaEspe.dto.DtoPresItem;
import ec.edu.espe.plantillaEspe.dto.DtoPresSubgrupo;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.PresGrupo;
import ec.edu.espe.plantillaEspe.model.PresItem;
import ec.edu.espe.plantillaEspe.model.PresSubgrupo;
import ec.edu.espe.plantillaEspe.service.IService.IServicePresSubgrupo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicePresSubgrupo implements IServicePresSubgrupo {

    private final DaoPresSubgrupo daoPresSubgrupo;
    private final UserInfoService userInfoService;
    private final DaoPresItem daoPresItem;
    private final DaoPresGrupo daoPresGrupo;
    private final ServicePresItem servicePresItem;

    @Autowired
    public ServicePresSubgrupo(DaoPresSubgrupo daoPresSubgrupo,
                              UserInfoService userInfoService,
                              DaoPresItem daoPresItem,
                              DaoPresGrupo daoPresGrupo,
                              ServicePresItem servicePresItem) {
        this.daoPresSubgrupo = daoPresSubgrupo;
        this.userInfoService = userInfoService;
        this.daoPresItem = daoPresItem;
        this.daoPresGrupo = daoPresGrupo;
        this.servicePresItem = servicePresItem;
    }

    @Override
    public DtoPresSubgrupo find(String codigo) {
        validateCodigo(codigo);
        return daoPresSubgrupo.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un subgrupo con el código especificado."));
    }

    @Override
    public List<DtoPresSubgrupo> findAll() {
        try {
            return daoPresSubgrupo.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los subgrupos.", e);
        }
    }

    @Override
    public List<DtoPresSubgrupo> findAllActivos() {
        try {
            return daoPresSubgrupo.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los subgrupos activos.", e);
        }
    }

    @Override
    public Page<DtoPresSubgrupo> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPresSubgrupo.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los subgrupos paginados.", e);
        }
    }

    @Override
    public Page<DtoPresSubgrupo> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPresSubgrupo.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los subgrupos activos paginados.", e);
        }
    }

    @Override
    public Page<DtoPresSubgrupo> findByPresGrupo_Codigo(String codigo, Pageable pageable, Map<String, String> searchCriteria) {
        validateCodigo(codigo);
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoPresSubgrupo.findByPresGrupo_CodigoAndEstado(codigo, Estado.A, pageable)
                    .map(this::convertToDto);
        } else {
            List<DtoPresSubgrupo> subgrupos = daoPresSubgrupo.findByPresGrupo_CodigoAndEstado(codigo, Estado.A).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(subgrupos, searchCriteria, pageable);
        }
    }

    @Override
    @Transactional
    public DtoPresSubgrupo save(DtoPresSubgrupo dtoPresSubgrupo, String accessToken) {
        validarNuevoSubgrupo(dtoPresSubgrupo);
        String username = obtenerNombreUsuario(accessToken);

        Optional<PresSubgrupo> existente = daoPresSubgrupo.findByCodigo(dtoPresSubgrupo.getCodigo());
        if (existente.isPresent()) {
            PresSubgrupo subgrupo = existente.get();
            if (Estado.A.equals(subgrupo.getEstado())) {
                throw new DataValidationException("Ya existe un subgrupo activo con el código especificado.");
            }
            // Reactivar registro inactivo
            subgrupo.setEstado(Estado.A);
            subgrupo.setDescripcion(dtoPresSubgrupo.getDescripcion());
            subgrupo.setNombre(dtoPresSubgrupo.getNombre());
            subgrupo.setFechaCreacion(new Date());
            subgrupo.setAlineacion(TipoAlineacion.OPERATIVA);
            subgrupo.setUsuarioCreacion(username);

            // Relación padre: Grupo
            if (dtoPresSubgrupo.getCodigoGrupoFk() != null) {
                PresGrupo grupo = daoPresGrupo.findByCodigo(dtoPresSubgrupo.getCodigoGrupoFk())
                        .orElseThrow(() -> new DataValidationException("No se encontró el grupo padre especificado."));
                subgrupo.setPresGrupo(grupo);
            } else {
                subgrupo.setPresGrupo(null);
            }

            vincularItems(subgrupo, dtoPresSubgrupo.getPresItemList(), username);
            return convertToDto(daoPresSubgrupo.save(subgrupo));
        }

        PresSubgrupo presSubgrupo = crearPresSubgrupo(dtoPresSubgrupo, username);

        // Relación padre: Grupo
        if (dtoPresSubgrupo.getCodigoGrupoFk() != null) {
            PresGrupo grupo = daoPresGrupo.findByCodigo(dtoPresSubgrupo.getCodigoGrupoFk())
                    .orElseThrow(() -> new DataValidationException("No se encontró el grupo padre especificado."));
            presSubgrupo.setPresGrupo(grupo);
        } else {
            presSubgrupo.setPresGrupo(null);
        }

        presSubgrupo.setEstado(Estado.A);
        vincularItems(presSubgrupo, dtoPresSubgrupo.getPresItemList(), username);

        return convertToDto(daoPresSubgrupo.save(presSubgrupo));
    }

    @Override
    @Transactional
    public DtoPresSubgrupo update(DtoPresSubgrupo dtoPresSubgrupo, String accessToken) {
        validateCodigo(dtoPresSubgrupo.getCodigo());
        validateDtoPresSubgrupo(dtoPresSubgrupo);
        String username = obtenerNombreUsuario(accessToken);

        PresSubgrupo presSubgrupo = obtenerPresSubgrupoExistente(dtoPresSubgrupo.getCodigo());

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoPresSubgrupo.getEstado()) && Estado.A.equals(presSubgrupo.getEstado())) {
            throw new DataValidationException("Para desactivar un subgrupo use el método delete()");
        }

        actualizarDatosBasicos(presSubgrupo, dtoPresSubgrupo, username);

        // Relación padre: Grupo
        if (dtoPresSubgrupo.getCodigoGrupoFk() != null) {
            PresGrupo grupo = daoPresGrupo.findByCodigo(dtoPresSubgrupo.getCodigoGrupoFk())
                    .orElseThrow(() -> new DataValidationException("No se encontró el grupo padre especificado."));
            presSubgrupo.setPresGrupo(grupo);
        } else {
            presSubgrupo.setPresGrupo(null);
        }

        actualizarItemsAsociados(presSubgrupo, dtoPresSubgrupo.getPresItemList(), username);

        return convertToDto(daoPresSubgrupo.save(presSubgrupo));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        PresSubgrupo presSubgrupo = daoPresSubgrupo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un subgrupo con el código especificado."));

        String username = obtenerNombreUsuario(accessToken);

        presSubgrupo.setEstado(Estado.I);
        presSubgrupo.setFechaModificacion(new Date());
        presSubgrupo.setUsuarioModificacion(username);
        presSubgrupo.setPresGrupo(null); // Desvincular grupo padre
        daoPresSubgrupo.save(presSubgrupo);

        // Desvincular items asociados
        if (presSubgrupo.getPresItems() != null) {
            for (PresItem presItem : presSubgrupo.getPresItems()) {
                servicePresItem.delete(presItem.getCodigo(), accessToken);
            }
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del subgrupo no puede ser nulo o vacío.");
        }
    }

    private void validateDtoPresSubgrupo(DtoPresSubgrupo dtoPresSubgrupo) {
        if (dtoPresSubgrupo == null) {
            throw new DataValidationException("DtoPresSubgrupo no puede ser nulo.");
        }
        validateCodigo(dtoPresSubgrupo.getCodigo());
        if (dtoPresSubgrupo.getDescripcion() == null || dtoPresSubgrupo.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción es requerida.");
        }
        if (dtoPresSubgrupo.getNombre() == null || dtoPresSubgrupo.getNombre().isEmpty()) {
            throw new DataValidationException("El nombre es requerido.");
        }
    }

    private void validarNuevoSubgrupo(DtoPresSubgrupo dtoPresSubgrupo) {
        validateDtoPresSubgrupo(dtoPresSubgrupo);
    }

    private String obtenerNombreUsuario(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private PresSubgrupo crearPresSubgrupo(DtoPresSubgrupo dto, String username) {
        PresSubgrupo presSubgrupo = new PresSubgrupo();
        presSubgrupo.setCodigo(dto.getCodigo());
        presSubgrupo.setFechaCreacion(new Date());
        presSubgrupo.setUsuarioCreacion(username);
        presSubgrupo.setDescripcion(dto.getDescripcion());
        presSubgrupo.setNombre(dto.getNombre());
        presSubgrupo.setAlineacion(TipoAlineacion.OPERATIVA);
        return presSubgrupo;
    }

    private void vincularItems(PresSubgrupo presSubgrupo, List<DtoPresItem> dtosItem, String username) {
        List<PresItem> items = obtenerItemsExistentes(dtosItem);
        actualizarReferenciasItems(items, presSubgrupo, username);
        presSubgrupo.setPresItems(items);
    }

    private List<PresItem> obtenerItemsExistentes(List<DtoPresItem> dtosItem) {
        if (dtosItem == null) return List.of();
        return dtosItem.stream()
                .map(dto -> daoPresItem.findByCodigo(dto.getCodigo())
                        .orElseThrow(() -> new DataValidationException(
                                "No se encontró un item con el código especificado: " + dto.getCodigo())))
                .collect(Collectors.toList());
    }

    private PresSubgrupo obtenerPresSubgrupoExistente(String codigo) {
        return daoPresSubgrupo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException(
                        "El subgrupo con el código especificado no existe."));
    }

    private void actualizarDatosBasicos(PresSubgrupo presSubgrupo,
                                        DtoPresSubgrupo dto,
                                        String username) {
        presSubgrupo.setFechaModificacion(new Date());
        presSubgrupo.setUsuarioModificacion(username);
        presSubgrupo.setEstado(Estado.A);
        presSubgrupo.setAlineacion(TipoAlineacion.OPERATIVA);
        presSubgrupo.setDescripcion(dto.getDescripcion());
        presSubgrupo.setNombre(dto.getNombre());
    }

    private void actualizarItemsAsociados(PresSubgrupo presSubgrupo,
                                          List<DtoPresItem> nuevosItems,
                                          String username) {
        List<PresItem> itemsActualizados = obtenerItemsExistentes(nuevosItems);
        actualizarReferenciasItems(itemsActualizados, presSubgrupo, username);
        desvincularItemsEliminados(presSubgrupo.getPresItems(), itemsActualizados, username);
        presSubgrupo.setPresItems(itemsActualizados);
    }

    private void actualizarReferenciasItems(List<PresItem> items,
                                           PresSubgrupo presSubgrupo,
                                           String username) {
        for (PresItem item : items) {
            item.setPresSubgrupo(presSubgrupo);
            item.setFechaModificacion(new Date());
            item.setUsuarioModificacion(username);
            daoPresItem.save(item);
        }
    }

    private void desvincularItemsEliminados(List<PresItem> itemsActuales,
                                            List<PresItem> itemsNuevos,
                                            String username) {
        if (itemsActuales == null) return;
        for (PresItem itemActual : itemsActuales) {
            if (itemsNuevos.stream().noneMatch(itemNuevo -> itemNuevo.getCodigo().equals(itemActual.getCodigo()))) {
                desvincularItem(itemActual, username);
            }
        }
    }

    private void desvincularItem(PresItem item, String username) {
        item.setPresSubgrupo(null);
        item.setFechaModificacion(new Date());
        item.setUsuarioModificacion(username);
        daoPresItem.save(item);
    }

    private DtoPresSubgrupo convertToDto(PresSubgrupo presSubgrupo) {
        if (presSubgrupo == null) {
            return null;
        }
        DtoPresSubgrupo dto = new DtoPresSubgrupo();
        dto.setId(presSubgrupo.getId());
        dto.setCodigo(presSubgrupo.getCodigo());
        dto.setFechaCreacion(presSubgrupo.getFechaCreacion());
        dto.setUsuarioCreacion(presSubgrupo.getUsuarioCreacion());
        dto.setFechaModificacion(presSubgrupo.getFechaModificacion());
        dto.setUsuarioModificacion(presSubgrupo.getUsuarioModificacion());
        dto.setEstado(presSubgrupo.getEstado());
        dto.setAlineacion(presSubgrupo.getAlineacion());
        dto.setDescripcion(presSubgrupo.getDescripcion());
        dto.setNombre(presSubgrupo.getNombre());

        // Relación padre: grupo
        if (presSubgrupo.getPresGrupo() != null) {
            dto.setCodigoGrupoFk(presSubgrupo.getPresGrupo().getCodigo());
        }

        // Relación hijo: items
        if (presSubgrupo.getPresItems() != null) {
            List<DtoPresItem> presItems = presSubgrupo.getPresItems().stream()
                    .map(pi -> {
                        DtoPresItem dtoPresItem = new DtoPresItem();
                        dtoPresItem.setId(pi.getId());
                        dtoPresItem.setCodigo(pi.getCodigo());
                        dtoPresItem.setFechaCreacion(pi.getFechaCreacion());
                        dtoPresItem.setUsuarioCreacion(pi.getUsuarioCreacion());
                        dtoPresItem.setFechaModificacion(pi.getFechaModificacion());
                        dtoPresItem.setUsuarioModificacion(pi.getUsuarioModificacion());
                        dtoPresItem.setEstado(pi.getEstado());
                        dtoPresItem.setDescripcion(pi.getDescripcion());
                        dtoPresItem.setNombre(pi.getNombre());
                        return dtoPresItem;
                    })
                    .collect(Collectors.toList());
            dto.setPresItemList(presItems);
        }
        return dto;
    }
}