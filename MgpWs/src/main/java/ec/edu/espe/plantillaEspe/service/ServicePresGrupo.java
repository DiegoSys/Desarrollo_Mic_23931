package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoPresGrupo;
import ec.edu.espe.plantillaEspe.dao.DaoPresNaturaleza;
import ec.edu.espe.plantillaEspe.dao.DaoPresSubgrupo;
import ec.edu.espe.plantillaEspe.dto.DtoPresGrupo;
import ec.edu.espe.plantillaEspe.dto.DtoPresSubgrupo;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.PresGrupo;
import ec.edu.espe.plantillaEspe.model.PresNaturaleza;
import ec.edu.espe.plantillaEspe.model.PresSubgrupo;
import ec.edu.espe.plantillaEspe.service.IService.IServicePresGrupo;
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
public class ServicePresGrupo implements IServicePresGrupo {

    private final DaoPresGrupo daoPresGrupo;
    private final UserInfoService userInfoService;
    private final DaoPresSubgrupo daoPresSubgrupo;
    private final DaoPresNaturaleza daoPresNaturaleza;

    @Autowired
    public ServicePresGrupo(DaoPresGrupo daoPresGrupo,
                            UserInfoService userInfoService,
                            DaoPresSubgrupo daoPresSubgrupo,
                            DaoPresNaturaleza daoPresNaturaleza) {
        this.daoPresGrupo = daoPresGrupo;
        this.userInfoService = userInfoService;
        this.daoPresSubgrupo = daoPresSubgrupo;
        this.daoPresNaturaleza = daoPresNaturaleza;
    }

    @Override
    public DtoPresGrupo find(String codigo) {
        validateCodigo(codigo);
        return daoPresGrupo.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un grupo con el código especificado."));
    }

    @Override
    public List<DtoPresGrupo> findAll() {
        try {
            return daoPresGrupo.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los grupos.", e);
        }
    }

    @Override
    public List<DtoPresGrupo> findAllActivos() {
        try {
            return daoPresGrupo.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los grupos activos.", e);
        }
    }

    @Override
    public Page<DtoPresGrupo> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPresGrupo.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los grupos paginados.", e);
        }
    }

    @Override
    public Page<DtoPresGrupo> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPresGrupo.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los grupos activos paginados.", e);
        }
    }

    @Override
    @Transactional
    public DtoPresGrupo save(DtoPresGrupo dtoPresGrupo, String accessToken) {
        validarNuevoGrupo(dtoPresGrupo);
        String username = obtenerNombreUsuario(accessToken);

        Optional<PresGrupo> existente = daoPresGrupo.findByCodigo(dtoPresGrupo.getCodigo());
        if (existente.isPresent()) {
            PresGrupo grupo = existente.get();
            if (Estado.A.equals(grupo.getEstado())) {
                throw new DataValidationException("Ya existe un grupo activo con el código especificado.");
            }
            // Reactivar registro inactivo
            grupo.setEstado(Estado.A);
            grupo.setAlineacion(TipoAlineacion.OPERATIVA);
            grupo.setDescripcion(dtoPresGrupo.getDescripcion());
            grupo.setNombre(dtoPresGrupo.getNombre());
            grupo.setFechaCreacion(new Date());
            grupo.setUsuarioCreacion(username);

            // Manejar la naturaleza
            if (dtoPresGrupo.getCodigoNaturalezaFk() != null) {
                PresNaturaleza naturaleza = daoPresNaturaleza.findByCodigo(dtoPresGrupo.getCodigoNaturalezaFk())
                        .orElseThrow(() -> new DataValidationException("No se encontró la naturaleza especificada."));
                grupo.setPresNaturaleza(naturaleza);
            }

            vincularSubgrupos(grupo, dtoPresGrupo.getPresSupGrupoList(), username);
            return convertToDto(daoPresGrupo.save(grupo));
        }

        PresGrupo presGrupo = crearPresGrupo(dtoPresGrupo, username);
        presGrupo.setEstado(Estado.A);
        presGrupo.setAlineacion(TipoAlineacion.OPERATIVA);

        // Manejar la naturaleza en nuevo grupo
        if (dtoPresGrupo.getCodigoNaturalezaFk() != null) {
            PresNaturaleza naturaleza = daoPresNaturaleza.findByCodigo(dtoPresGrupo.getCodigoNaturalezaFk())
                    .orElseThrow(() -> new DataValidationException("No se encontró la naturaleza especificada."));
            presGrupo.setPresNaturaleza(naturaleza);
        }

        vincularSubgrupos(presGrupo, dtoPresGrupo.getPresSupGrupoList(), username);

        return convertToDto(daoPresGrupo.save(presGrupo));
    }

    @Override
    @Transactional
    public DtoPresGrupo update(DtoPresGrupo dtoPresGrupo, String accessToken) {
        validateCodigo(dtoPresGrupo.getCodigo());
        validateDtoPresGrupo(dtoPresGrupo);
        String username = obtenerNombreUsuario(accessToken);

        PresGrupo presGrupo = obtenerPresGrupoExistente(dtoPresGrupo.getCodigo());

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoPresGrupo.getEstado()) && Estado.A.equals(presGrupo.getEstado())) {
            throw new DataValidationException("Para desactivar un grupo use el método delete()");
        }

        actualizarDatosBasicos(presGrupo, dtoPresGrupo, username);

        // Actualizar naturaleza si se proporciona
        if (dtoPresGrupo.getCodigoNaturalezaFk() != null) {
            PresNaturaleza naturaleza = daoPresNaturaleza.findByCodigo(dtoPresGrupo.getCodigoNaturalezaFk())
                    .orElseThrow(() -> new DataValidationException("No se encontró la naturaleza especificada."));
            presGrupo.setPresNaturaleza(naturaleza);
        } else {
            presGrupo.setPresNaturaleza(null);
        }

        presGrupo.setEstado(Estado.A);
        presGrupo.setAlineacion(TipoAlineacion.OPERATIVA);
        actualizarSubgruposAsociados(presGrupo, dtoPresGrupo.getPresSupGrupoList(), username);

        return convertToDto(daoPresGrupo.save(presGrupo));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        PresGrupo presGrupo = daoPresGrupo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un grupo con el código especificado."));

        String username = obtenerNombreUsuario(accessToken);

        presGrupo.setEstado(Estado.I);
        presGrupo.setFechaModificacion(new Date());
        presGrupo.setUsuarioModificacion(username);
        presGrupo.setPresNaturaleza(null); // Desvincular naturaleza
        daoPresGrupo.save(presGrupo);

        // Desvincular subgrupos asociados
        if (presGrupo.getPresSubgrupos() != null) {
            for (PresSubgrupo presSubgrupo : presGrupo.getPresSubgrupos()) {
                presSubgrupo.setPresGrupo(null);
                presSubgrupo.setFechaModificacion(new Date());
                presSubgrupo.setUsuarioModificacion(username);
                daoPresSubgrupo.save(presSubgrupo);
            }
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del grupo no puede ser nulo o vacío.");
        }
    }

    private void validateDtoPresGrupo(DtoPresGrupo dtoPresGrupo) {
        if (dtoPresGrupo == null) {
            throw new DataValidationException("DtoPresGrupo no puede ser nulo.");
        }
        validateCodigo(dtoPresGrupo.getCodigo());
        if (dtoPresGrupo.getDescripcion() == null || dtoPresGrupo.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción es requerida.");
        }
        if (dtoPresGrupo.getNombre() == null || dtoPresGrupo.getNombre().isEmpty()) {
            throw new DataValidationException("El nombre es requerido.");
        }
        if (dtoPresGrupo.getCodigoNaturalezaFk() != null) {
            validateCodigoNaturaleza(dtoPresGrupo.getCodigoNaturalezaFk());
        }
    }

    private void validateCodigoNaturaleza(String codigoNaturaleza) {
        if (codigoNaturaleza.isEmpty()) {
            throw new DataValidationException("El código de naturaleza no puede estar vacío cuando se proporciona.");
        }
    }

    private void validarNuevoGrupo(DtoPresGrupo dtoPresGrupo) {
        validateDtoPresGrupo(dtoPresGrupo);
    }

    private String obtenerNombreUsuario(String accessToken) {
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        return (String) userInfo.get("name");
    }

    private PresGrupo crearPresGrupo(DtoPresGrupo dto, String username) {
        PresGrupo presGrupo = new PresGrupo();
        presGrupo.setCodigo(dto.getCodigo());
        presGrupo.setFechaCreacion(new Date());
        presGrupo.setUsuarioCreacion(username);
        presGrupo.setEstado(Estado.A);
        presGrupo.setDescripcion(dto.getDescripcion());
        presGrupo.setNombre(dto.getNombre());
        return presGrupo;
    }

    private void vincularSubgrupos(PresGrupo presGrupo, List<DtoPresSubgrupo> dtosSubgrupo, String username) {
        List<PresSubgrupo> subgrupos = obtenerSubgruposExistentes(dtosSubgrupo);
        actualizarReferenciasSubgrupos(subgrupos, presGrupo, username);
        presGrupo.setPresSubgrupos(subgrupos);
    }

    private List<PresSubgrupo> obtenerSubgruposExistentes(List<DtoPresSubgrupo> dtosSubgrupo) {
        if (dtosSubgrupo == null) return List.of();
        return dtosSubgrupo.stream()
                .map(dto -> daoPresSubgrupo.findByCodigo(dto.getCodigo())
                        .orElseThrow(() -> new DataValidationException(
                                "No se encontró un subgrupo con el código especificado: " + dto.getCodigo())))
                .collect(Collectors.toList());
    }

    private PresGrupo obtenerPresGrupoExistente(String codigo) {
        return daoPresGrupo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException(
                        "El grupo con el código especificado no existe."));
    }

    private void actualizarDatosBasicos(PresGrupo presGrupo,
                                        DtoPresGrupo dto,
                                        String username) {
        presGrupo.setFechaModificacion(new Date());
        presGrupo.setUsuarioModificacion(username);
        presGrupo.setEstado(dto.getEstado());
        presGrupo.setDescripcion(dto.getDescripcion());
        presGrupo.setNombre(dto.getNombre());
    }

    private void actualizarSubgruposAsociados(PresGrupo presGrupo,
                                              List<DtoPresSubgrupo> nuevosSubgrupos,
                                              String username) {
        List<PresSubgrupo> subgruposActualizados = obtenerSubgruposExistentes(nuevosSubgrupos);
        actualizarReferenciasSubgrupos(subgruposActualizados, presGrupo, username);
        desvincularSubgruposEliminados(presGrupo.getPresSubgrupos(), subgruposActualizados, username);
        presGrupo.setPresSubgrupos(subgruposActualizados);
    }

    private void actualizarReferenciasSubgrupos(List<PresSubgrupo> subgrupos,
                                                PresGrupo presGrupo,
                                                String username) {
        for (PresSubgrupo subgrupo : subgrupos) {
            subgrupo.setPresGrupo(presGrupo);
            subgrupo.setFechaModificacion(new Date());
            subgrupo.setUsuarioModificacion(username);
            daoPresSubgrupo.save(subgrupo);
        }
    }

    private void desvincularSubgruposEliminados(List<PresSubgrupo> subgruposActuales,
                                                List<PresSubgrupo> subgruposNuevos,
                                                String username) {
        if (subgruposActuales == null) return;
        for (PresSubgrupo subgrupoActual : subgruposActuales) {
            if (subgruposNuevos.stream().noneMatch(s -> s.getCodigo().equals(subgrupoActual.getCodigo()))) {
                desvincularSubgrupo(subgrupoActual, username);
            }
        }
    }

    private void desvincularSubgrupo(PresSubgrupo subgrupo, String username) {
        subgrupo.setPresGrupo(null);
        subgrupo.setFechaModificacion(new Date());
        subgrupo.setUsuarioModificacion(username);
        daoPresSubgrupo.save(subgrupo);
    }

    private DtoPresGrupo convertToDto(PresGrupo presGrupo) {
        if (presGrupo == null) {
            return null;
        }
        DtoPresGrupo dto = new DtoPresGrupo();
        dto.setId(presGrupo.getId());
        dto.setCodigo(presGrupo.getCodigo());
        dto.setFechaCreacion(presGrupo.getFechaCreacion());
        dto.setUsuarioCreacion(presGrupo.getUsuarioCreacion());
        dto.setFechaModificacion(presGrupo.getFechaModificacion());
        dto.setUsuarioModificacion(presGrupo.getUsuarioModificacion());
        dto.setEstado(presGrupo.getEstado());
        dto.setDescripcion(presGrupo.getDescripcion());
        dto.setNombre(presGrupo.getNombre());
        dto.setAlineacion(presGrupo.getAlineacion());

        // Añadir el código de naturaleza si existe
        if (presGrupo.getPresNaturaleza() != null) {
            dto.setCodigoNaturalezaFk(presGrupo.getPresNaturaleza().getCodigo());
        }

        if (presGrupo.getPresSubgrupos() != null) {
            List<DtoPresSubgrupo> presSubgrupos = presGrupo.getPresSubgrupos().stream()
                    .map(ps -> {
                        DtoPresSubgrupo dtoPresSubgrupo = new DtoPresSubgrupo();
                        dtoPresSubgrupo.setId(ps.getId());
                        dtoPresSubgrupo.setCodigo(ps.getCodigo());
                        dtoPresSubgrupo.setFechaCreacion(ps.getFechaCreacion());
                        dtoPresSubgrupo.setUsuarioCreacion(ps.getUsuarioCreacion());
                        dtoPresSubgrupo.setFechaModificacion(ps.getFechaModificacion());
                        dtoPresSubgrupo.setUsuarioModificacion(ps.getUsuarioModificacion());
                        dtoPresSubgrupo.setEstado(ps.getEstado());
                        dtoPresSubgrupo.setDescripcion(ps.getDescripcion());
                        dtoPresSubgrupo.setNombre(ps.getNombre());
                        return dtoPresSubgrupo;
                    })
                    .collect(Collectors.toList());
            dto.setPresSupGrupoList(presSubgrupos);
        }
        return dto;
    }
}