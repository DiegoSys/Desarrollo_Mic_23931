package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoPresGrupo;
import ec.edu.espe.plantillaEspe.dao.DaoPresNaturaleza;
import ec.edu.espe.plantillaEspe.dto.DtoPresGrupo;
import ec.edu.espe.plantillaEspe.dto.DtoPresNaturaleza;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.PresGrupo;
import ec.edu.espe.plantillaEspe.model.PresNaturaleza;
import ec.edu.espe.plantillaEspe.service.IService.IServicePresNaturaleza;
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
public class ServicePresNaturaleza implements IServicePresNaturaleza {

    private final DaoPresNaturaleza daoPresNaturaleza;
    private final UserInfoService userInfoService;
    private final DaoPresGrupo daoPresGrupo;

    @Autowired
    public ServicePresNaturaleza(DaoPresNaturaleza daoPresNaturaleza,
                                 UserInfoService userInfoService,
                                 DaoPresGrupo daoPresGrupo) {
        this.daoPresNaturaleza = daoPresNaturaleza;
        this.userInfoService = userInfoService;
        this.daoPresGrupo = daoPresGrupo;
    }

    @Override
    public DtoPresNaturaleza find(String codigo) {
        validateCodigo(codigo);
        return daoPresNaturaleza.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró una naturaleza con el código especificado."));
    }

    @Override
    public List<DtoPresNaturaleza> findAll() {
        try {
            return daoPresNaturaleza.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todas las naturalezas.", e);
        }
    }

    @Override
    public List<DtoPresNaturaleza> findAllActivos() {
        try {
            return daoPresNaturaleza.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las naturalezas activas.", e);
        }
    }

    @Override
    public Page<DtoPresNaturaleza> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPresNaturaleza.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las naturalezas paginadas.", e);
        }
    }

    @Override
    public Page<DtoPresNaturaleza> findAllActivos(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPresNaturaleza.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las naturalezas activas paginadas.", e);
        }
    }

    @Override
    @Transactional
    public DtoPresNaturaleza save(DtoPresNaturaleza dtoPresNaturaleza, String accessToken) {
        validateDtoPresNaturaleza(dtoPresNaturaleza);

        Optional<PresNaturaleza> existente = daoPresNaturaleza.findByCodigo(dtoPresNaturaleza.getCodigo());
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        if (existente.isPresent()) {
            PresNaturaleza naturaleza = existente.get();
            if (Estado.A.equals(naturaleza.getEstado())) {
                throw new DataValidationException("Ya existe una naturaleza activa con el código especificado.");
            }
            // Reactivar registro inactivo
            naturaleza.setEstado(Estado.A);
            naturaleza.setAlineacion(TipoAlineacion.OPERATIVA);
            naturaleza.setDescripcion(dtoPresNaturaleza.getDescripcion());
            naturaleza.setNombre(dtoPresNaturaleza.getNombre());
            naturaleza.setFechaCreacion(new Date());
            naturaleza.setUsuarioCreacion(username);
            vincularGrupos(naturaleza, dtoPresNaturaleza.getPresGrupoList(), username);
            return convertToDto(daoPresNaturaleza.save(naturaleza));
        }

        PresNaturaleza presNaturaleza = new PresNaturaleza();
        presNaturaleza.setCodigo(dtoPresNaturaleza.getCodigo());
        presNaturaleza.setFechaCreacion(new Date());
        presNaturaleza.setUsuarioCreacion(username);
        presNaturaleza.setEstado(Estado.A);
        presNaturaleza.setAlineacion(TipoAlineacion.OPERATIVA);
        presNaturaleza.setDescripcion(dtoPresNaturaleza.getDescripcion());
        presNaturaleza.setNombre(dtoPresNaturaleza.getNombre());
        vincularGrupos(presNaturaleza, dtoPresNaturaleza.getPresGrupoList(), username);

        return convertToDto(daoPresNaturaleza.save(presNaturaleza));
    }

    @Override
    @Transactional
    public DtoPresNaturaleza update(DtoPresNaturaleza dtoPresNaturaleza, String accessToken) {
        validateDtoPresNaturaleza(dtoPresNaturaleza);

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        PresNaturaleza presNaturaleza = daoPresNaturaleza.findByCodigo(dtoPresNaturaleza.getCodigo())
                .orElseThrow(() -> new DataValidationException("La naturaleza con el código especificado no existe."));

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoPresNaturaleza.getEstado()) && Estado.A.equals(presNaturaleza.getEstado())) {
            throw new DataValidationException("Para desactivar una naturaleza use el método delete()");
        }

        presNaturaleza.setFechaModificacion(new Date());
        presNaturaleza.setUsuarioModificacion(username);
        presNaturaleza.setEstado(Estado.A);
        presNaturaleza.setAlineacion(TipoAlineacion.OPERATIVA);
        presNaturaleza.setDescripcion(dtoPresNaturaleza.getDescripcion());
        presNaturaleza.setNombre(dtoPresNaturaleza.getNombre());
        actualizarGruposAsociados(presNaturaleza, dtoPresNaturaleza.getPresGrupoList(), username);

        return convertToDto(daoPresNaturaleza.save(presNaturaleza));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        PresNaturaleza presNaturaleza = daoPresNaturaleza.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró una naturaleza con el código especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        presNaturaleza.setUsuarioModificacion(username);
        presNaturaleza.setEstado(Estado.I);
        presNaturaleza.setFechaModificacion(new Date());
        daoPresNaturaleza.save(presNaturaleza);

        // Desvincular grupos asociados
        if (presNaturaleza.getPresGrupo() != null) {
            for (PresGrupo presGrupo : presNaturaleza.getPresGrupo()) {
                presGrupo.setPresNaturaleza(null);
                presGrupo.setFechaModificacion(new Date());
                presGrupo.setUsuarioModificacion(username);
                daoPresGrupo.save(presGrupo);
            }
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código de la naturaleza no puede ser nulo o vacío.");
        }
    }

    private void validateDtoPresNaturaleza(DtoPresNaturaleza dtoPresNaturaleza) {
        if (dtoPresNaturaleza == null) {
            throw new DataValidationException("DtoPresNaturaleza no puede ser nulo.");
        }
        validateCodigo(dtoPresNaturaleza.getCodigo());
        if (dtoPresNaturaleza.getDescripcion() == null || dtoPresNaturaleza.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoPresNaturaleza.getNombre() == null || dtoPresNaturaleza.getNombre().isEmpty()) {
            throw new DataValidationException("Nombre es requerido.");
        }
    }

    private void vincularGrupos(PresNaturaleza presNaturaleza, List<DtoPresGrupo> dtosGrupo, String username) {
        List<PresGrupo> grupos = obtenerGruposExistentes(dtosGrupo);
        actualizarReferenciasGrupos(grupos, presNaturaleza, username);
        presNaturaleza.setPresGrupo(grupos);
    }

    private List<PresGrupo> obtenerGruposExistentes(List<DtoPresGrupo> dtosGrupo) {
        if (dtosGrupo == null) return List.of();
        return dtosGrupo.stream()
                .map(dto -> daoPresGrupo.findByCodigo(dto.getCodigo())
                        .orElseThrow(() -> new DataValidationException(
                                "No se encontró un grupo con el código especificado: " + dto.getCodigo())))
                .collect(Collectors.toList());
    }

    private void actualizarGruposAsociados(PresNaturaleza presNaturaleza,
                                           List<DtoPresGrupo> nuevosGrupos,
                                           String username) {
        List<PresGrupo> gruposActualizados = obtenerGruposExistentes(nuevosGrupos);
        actualizarReferenciasGrupos(gruposActualizados, presNaturaleza, username);
        desvincularGruposEliminados(presNaturaleza.getPresGrupo(), gruposActualizados, username);
        presNaturaleza.setPresGrupo(gruposActualizados);
    }

    private void actualizarReferenciasGrupos(List<PresGrupo> grupos,
                                             PresNaturaleza presNaturaleza,
                                             String username) {
        for (PresGrupo grupo : grupos) {
            grupo.setPresNaturaleza(presNaturaleza);
            grupo.setFechaModificacion(new Date());
            grupo.setUsuarioModificacion(username);
            daoPresGrupo.save(grupo);
        }
    }

    private void desvincularGruposEliminados(List<PresGrupo> gruposActuales,
                                             List<PresGrupo> gruposNuevos,
                                             String username) {
        if (gruposActuales == null) return;
        for (PresGrupo grupoActual : gruposActuales) {
            if (gruposNuevos.stream().noneMatch(g -> g.getCodigo().equals(grupoActual.getCodigo()))) {
                grupoActual.setPresNaturaleza(null);
                grupoActual.setFechaModificacion(new Date());
                grupoActual.setUsuarioModificacion(username);
                daoPresGrupo.save(grupoActual);
            }
        }
    }

    private DtoPresNaturaleza convertToDto(PresNaturaleza presNaturaleza) {
        if (presNaturaleza == null) {
            return null;
        }
        DtoPresNaturaleza dto = new DtoPresNaturaleza();
        dto.setId(presNaturaleza.getId());
        dto.setCodigo(presNaturaleza.getCodigo());
        dto.setFechaCreacion(presNaturaleza.getFechaCreacion());
        dto.setUsuarioCreacion(presNaturaleza.getUsuarioCreacion());
        dto.setFechaModificacion(presNaturaleza.getFechaModificacion());
        dto.setUsuarioModificacion(presNaturaleza.getUsuarioModificacion());
        dto.setEstado(presNaturaleza.getEstado());
        dto.setDescripcion(presNaturaleza.getDescripcion());
        dto.setNombre(presNaturaleza.getNombre());

        if (presNaturaleza.getPresGrupo() != null) {
            List<DtoPresGrupo> presGrupos = presNaturaleza.getPresGrupo().stream()
                    .map(pg -> {
                        DtoPresGrupo dtoPresGrupo = new DtoPresGrupo();
                        dtoPresGrupo.setId(pg.getId());
                        dtoPresGrupo.setCodigo(pg.getCodigo());
                        dtoPresGrupo.setCodigoNaturalezaFk(pg.getPresNaturaleza().getCodigo());
                        dtoPresGrupo.setFechaCreacion(pg.getFechaCreacion());
                        dtoPresGrupo.setUsuarioCreacion(pg.getUsuarioCreacion());
                        dtoPresGrupo.setFechaModificacion(pg.getFechaModificacion());
                        dtoPresGrupo.setUsuarioModificacion(pg.getUsuarioModificacion());
                        dtoPresGrupo.setEstado(pg.getEstado());
                        dtoPresGrupo.setDescripcion(pg.getDescripcion());
                        dtoPresGrupo.setNombre(pg.getNombre());
                        return dtoPresGrupo;
                    })
                    .collect(Collectors.toList());
            dto.setPresGrupoList(presGrupos);
        }
        return dto;
    }
}