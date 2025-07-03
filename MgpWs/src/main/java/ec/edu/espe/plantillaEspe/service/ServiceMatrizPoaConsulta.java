package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoActividad;
import ec.edu.espe.plantillaEspe.dao.DaoPresItem;
import ec.edu.espe.plantillaEspe.dao.DaoPOA;
import ec.edu.espe.plantillaEspe.dto.*;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.*;
import ec.edu.espe.plantillaEspe.service.IService.IServiceMatrizPoaConsulta;
import ec.edu.espe.plantillaEspe.util.GenericSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceMatrizPoaConsulta implements IServiceMatrizPoaConsulta {

    private final DaoActividad daoActividad;
    private final DaoPresItem daoPresItem;
    private final DaoPOA daoPOA;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceMatrizPoaConsulta(DaoActividad daoActividad, DaoPresItem daoPresItem, DaoPOA daoPOA, UserInfoService userInfoService) {
        this.daoActividad = daoActividad;
        this.daoPresItem = daoPresItem;
        this.daoPOA = daoPOA;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoPOA find(Long id) {
        if (id == null) throw new DataValidationException("El id no puede ser nulo.");
        POA poa = daoPOA.findById(id).orElseThrow(() -> new DataValidationException("POA no encontrado"));
        return convertToDtoPOA(poa);
    }

    @Override
    public Page<DtoMatrizPoaConsulta> findAll(Pageable pageable) {
        if (pageable == null) throw new DataValidationException("Los parámetros de paginación son requeridos.");
        return daoPOA.findAll(pageable).map(this::mapToDtoMatrizPoaConsulta);
    }

    @Override
    public List<DtoMatrizPoaConsulta> findAll() {
        return daoPOA.findAll().stream().map(this::mapToDtoMatrizPoaConsulta).collect(Collectors.toList());
    }

    @Override
    public List<DtoMatrizPoaConsulta> findAllActivos() {
        return daoPOA.findByEstado(Estado.A).stream().map(this::mapToDtoMatrizPoaConsulta).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DtoPOA save(DtoPOA dtoPOA, String accessToken) {
        validateDtoPOA(dtoPOA);
    
        // Verificar si ya existe un POA con la misma actividad e item activos
        Optional<POA> poaExistente = daoPOA.findAll().stream()
            .filter(p -> Objects.equals(p.getActividad() != null ? p.getActividad().getId() : null, dtoPOA.getActividadID())
                     && Objects.equals(p.getItem() != null ? p.getItem().getId() : null, dtoPOA.getItemID()))
            .findFirst();
    
        if (poaExistente.isPresent()) {
            POA poa = poaExistente.get();
            if (Estado.A.equals(poa.getEstado())) {
                throw new DataValidationException("Ya existe un POA activo con la actividad e item especificados.");
            } else {
                // Reactivar registro inactivo
                poa.setEstado(Estado.A);
                poa.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                poa.setUsuarioCreacion(username);
                return convertToDtoPOA(daoPOA.save(poa));
            }
        }
    
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");
    
        POA poa = new POA();
        poa.setEstado(Estado.A); // Nuevo registro siempre activo
        poa.setFechaCreacion(new Date());
        poa.setUsuarioCreacion(username);
    
        if (dtoPOA.getActividadID() != null) {
            poa.setActividad(daoActividad.findById(dtoPOA.getActividadID()).orElse(null));
        }
        if (dtoPOA.getItemID() != null) {
            poa.setItem(daoPresItem.findById(dtoPOA.getItemID()).orElse(null));
        }
    
        return convertToDtoPOA(daoPOA.save(poa));
    }
    
    @Override
    @Transactional
    public DtoPOA update(Long id, DtoPOA dtoPOA, String accessToken) {
        if (id == null) throw new DataValidationException("El id no puede ser nulo.");
        validateDtoPOA(dtoPOA);
    
        POA poa = daoPOA.findById(id).orElseThrow(() -> new DataValidationException("POA no encontrado"));
    
        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoPOA.getEstado()) && Estado.A.equals(poa.getEstado())) {
            throw new DataValidationException("Para desactivar un POA use el método delete()");
        }
    
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");
    
        poa.setEstado(Estado.A);
        poa.setFechaModificacion(new Date());
        poa.setUsuarioModificacion(username);
    
        if (dtoPOA.getActividadID() != null) {
            poa.setActividad(daoActividad.findById(dtoPOA.getActividadID()).orElse(null));
        }
        if (dtoPOA.getItemID() != null) {
            poa.setItem(daoPresItem.findById(dtoPOA.getItemID()).orElse(null));
        }
    
        return convertToDtoPOA(daoPOA.save(poa));
    }

    @Override
    @Transactional
    public void delete(Long id, String accessToken) {
        if (id == null) throw new DataValidationException("El id no puede ser nulo.");
        POA poa = daoPOA.findById(id).orElseThrow(() -> new DataValidationException("POA no encontrado"));
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");
        poa.setUsuarioModificacion(username);
        poa.setFechaModificacion(new Date());
        poa.setEstado(Estado.I);
        daoPOA.save(poa);
    }

     @Override
    public Page<DtoMatrizPoaConsulta> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
    
        // 1. Traer todos los POA activos
        List<POA> poas = daoPOA.findByEstado(Estado.A);
    
        // 2. Mapear cada POA a DtoMatrizPoaConsulta usando la relación con Actividad e Item
        List<DtoMatrizPoaConsulta> matriz = poas.stream()
            .map(poa -> {
                DtoMatrizPoaConsulta dto = new DtoMatrizPoaConsulta();
                // Jerarquía de la Actividad
                if (poa.getActividad() != null) {
                    dto.setPrograma(mapPrograma(poa.getActividad().getProyecto().getSubprograma().getPrograma()));
                    dto.setSubprograma(mapSubPrograma(poa.getActividad().getProyecto().getSubprograma()));
                    dto.setProyecto(mapProyecto(poa.getActividad().getProyecto()));
                    dto.setActividad(mapActividad(poa.getActividad()));
                }
                // Jerarquía del Item
                if (poa.getItem() != null) {
                    DtoPresItem dtoItem = mapPresItem(poa.getItem());
                    DtoPresSubgrupo dtoSubgrupo = poa.getItem().getPresSubgrupo() != null ? mapPresSubgrupo(poa.getItem().getPresSubgrupo()) : null;
                    DtoPresGrupo dtoGrupo = (poa.getItem().getPresSubgrupo() != null && poa.getItem().getPresSubgrupo().getPresGrupo() != null) ? mapPresGrupo(poa.getItem().getPresSubgrupo().getPresGrupo()) : null;
                    DtoPresNaturaleza dtoNaturaleza = (poa.getItem().getPresSubgrupo() != null && poa.getItem().getPresSubgrupo().getPresGrupo() != null && poa.getItem().getPresSubgrupo().getPresGrupo().getPresNaturaleza() != null) ? mapPresNaturaleza(poa.getItem().getPresSubgrupo().getPresGrupo().getPresNaturaleza()) : null;
                    dto.setPresNaturaleza(dtoNaturaleza);
                    dto.setPresGrupo(dtoGrupo);
                    dto.setPresSubgrupo(dtoSubgrupo);
                    dto.setItem(dtoItem);
                }
                dto.setExisteRelacion(true);
                dto.setObservacion("");
                return dto;
            })
            .toList();
    
        // 3. Filtrar y paginar
        if (searchCriteria != null && !searchCriteria.isEmpty()) {
            return GenericSearchUtil.search(matriz, searchCriteria, pageable);
        } else {
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), matriz.size());
            List<DtoMatrizPoaConsulta> pageContent = matriz.subList(start, end);
            return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, matriz.size());
        }
    }
    

    
    // Métodos de mapeo y validación

    private void validateDtoPOA(DtoPOA dtoPOA) {
        if (dtoPOA == null) throw new DataValidationException("DtoPOA no puede ser nulo.");
    }

    private DtoPOA convertToDtoPOA(POA poa) {
        DtoPOA dto = new DtoPOA();
        dto.setId(poa.getId());
        dto.setEstado(poa.getEstado());
        dto.setUsuarioCreacion(poa.getUsuarioCreacion());
        dto.setFechaCreacion(poa.getFechaCreacion());
        dto.setUsuarioModificacion(poa.getUsuarioModificacion());
        dto.setFechaModificacion(poa.getFechaModificacion());
        dto.setActividadID(poa.getActividad() != null ? poa.getActividad().getId() : null);
        dto.setItemID(poa.getItem() != null ? poa.getItem().getId() : null);
        return dto;
    }

    private DtoMatrizPoaConsulta mapToDtoMatrizPoaConsulta(POA poa) {
        DtoMatrizPoaConsulta dto = new DtoMatrizPoaConsulta();
        if (poa.getActividad() != null) {
            dto.setPrograma(mapPrograma(poa.getActividad().getProyecto().getSubprograma().getPrograma()));
            dto.setSubprograma(mapSubPrograma(poa.getActividad().getProyecto().getSubprograma()));
            dto.setProyecto(mapProyecto(poa.getActividad().getProyecto()));
            dto.setActividad(mapActividad(poa.getActividad()));
        }
        if (poa.getItem() != null) {
            DtoPresItem dtoItem = mapPresItem(poa.getItem());
            DtoPresSubgrupo dtoSubgrupo = poa.getItem().getPresSubgrupo() != null ? mapPresSubgrupo(poa.getItem().getPresSubgrupo()) : null;
            DtoPresGrupo dtoGrupo = (poa.getItem().getPresSubgrupo() != null && poa.getItem().getPresSubgrupo().getPresGrupo() != null) ? mapPresGrupo(poa.getItem().getPresSubgrupo().getPresGrupo()) : null;
            DtoPresNaturaleza dtoNaturaleza = (poa.getItem().getPresSubgrupo() != null && poa.getItem().getPresSubgrupo().getPresGrupo() != null && poa.getItem().getPresSubgrupo().getPresGrupo().getPresNaturaleza() != null) ? mapPresNaturaleza(poa.getItem().getPresSubgrupo().getPresGrupo().getPresNaturaleza()) : null;
            dto.setPresNaturaleza(dtoNaturaleza);
            dto.setPresGrupo(dtoGrupo);
            dto.setPresSubgrupo(dtoSubgrupo);
            dto.setItem(dtoItem);
        }
        dto.setExisteRelacion(true);
        dto.setObservacion("");
        return dto;
    }

    // Métodos de mapeo para Programa, SubPrograma, Proyecto, Actividad, PresItem, PresSubgrupo, PresGrupo, PresNaturaleza
    // (idénticos a los que ya tienes implementados en tu archivo actual)
    private DtoPrograma mapPrograma(Programa p) {
        if (p == null) return null;
        DtoPrograma dto = new DtoPrograma();
        dto.setId(p.getId());
        dto.setCodigo(p.getCodigo());
        dto.setDescripcion(p.getDescripcion());
        dto.setNombre(p.getNombre());
        dto.setUsuarioCreacion(p.getUsuarioCreacion());
        dto.setFechaCreacion(p.getFechaCreacion());
        dto.setUsuarioModificacion(p.getUsuarioModificacion());
        dto.setFechaModificacion(p.getFechaModificacion());
        dto.setEstado(p.getEstado());
        dto.setAlineacion(p.getAlineacion());
        return dto;
    }

    private DtoSubPrograma mapSubPrograma(SubPrograma sp) {
        if (sp == null) return null;
        DtoSubPrograma dto = new DtoSubPrograma();
        dto.setId(sp.getId());
        dto.setCodigo(sp.getCodigo());
        dto.setDescripcion(sp.getDescripcion());
        dto.setNombre(sp.getNombre());
        dto.setUsuarioCreacion(sp.getUsuarioCreacion());
        dto.setFechaCreacion(sp.getFechaCreacion());
        dto.setUsuarioModificacion(sp.getUsuarioModificacion());
        dto.setFechaModificacion(sp.getFechaModificacion());
        dto.setEstado(sp.getEstado());
        dto.setAlineacion(sp.getAlineacion());
        dto.setProgramaId(sp.getPrograma() != null ? sp.getPrograma().getId() : null);
        return dto;
    }

    private DtoProyecto mapProyecto(Proyecto pr) {
        if (pr == null) return null;
        DtoProyecto dto = new DtoProyecto();
        dto.setId(pr.getId());
        dto.setCodigo(pr.getCodigo());
        dto.setDescripcion(pr.getDescripcion());
        dto.setNombre(pr.getNombre());
        dto.setUsuarioCreacion(pr.getUsuarioCreacion());
        dto.setFechaCreacion(pr.getFechaCreacion());
        dto.setUsuarioModificacion(pr.getUsuarioModificacion());
        dto.setFechaModificacion(pr.getFechaModificacion());
        dto.setEstado(pr.getEstado());
        dto.setAlineacion(pr.getAlineacion());
        dto.setSubProgramaId(pr.getSubprograma() != null ? pr.getSubprograma().getId() : null);
        dto.setProgramaId(pr.getPrograma() != null ? pr.getPrograma().getId() : null);
        return dto;
    }

    private DtoActividad mapActividad(Actividad a) {
        if (a == null) return null;
        DtoActividad dto = new DtoActividad();
        dto.setId(a.getId());
        dto.setCodigo(a.getCodigo());
        dto.setDescripcion(a.getDescripcion());
        dto.setNombre(a.getNombre());
        dto.setUsuarioCreacion(a.getUsuarioCreacion());
        dto.setFechaCreacion(a.getFechaCreacion());
        dto.setUsuarioModificacion(a.getUsuarioModificacion());
        dto.setFechaModificacion(a.getFechaModificacion());
        dto.setEstado(a.getEstado());
        dto.setAlineacion(a.getAlineacion());
        dto.setProyectoId(a.getProyecto() != null ? a.getProyecto().getId() : null);
        dto.setSubProgramaId(a.getProyecto() != null && a.getProyecto().getSubprograma() != null ? a.getProyecto().getSubprograma().getId() : null);
        dto.setProgramaId(a.getProyecto() != null && a.getProyecto().getSubprograma() != null && a.getProyecto().getSubprograma().getPrograma() != null ? a.getProyecto().getSubprograma().getPrograma().getId() : null);
        return dto;
    }

    private DtoPresItem mapPresItem(PresItem i) {
        if (i == null) return null;
        DtoPresItem dto = new DtoPresItem();
        dto.setId(i.getId());
        dto.setCodigo(i.getCodigo());
        dto.setCodigoSubGrupoFk(i.getPresSubgrupo() != null ? i.getPresSubgrupo().getCodigo() : null);
        dto.setNombre(i.getNombre());
        dto.setDescripcion(i.getDescripcion());
        dto.setUsuarioCreacion(i.getUsuarioCreacion());
        dto.setFechaCreacion(i.getFechaCreacion());
        dto.setUsuarioModificacion(i.getUsuarioModificacion());
        dto.setFechaModificacion(i.getFechaModificacion());
        dto.setEstado(i.getEstado());
        dto.setAlineacion(i.getAlineacion());
        return dto;
    }

    private DtoPresSubgrupo mapPresSubgrupo(PresSubgrupo sg) {
        if (sg == null) return null;
        DtoPresSubgrupo dto = new DtoPresSubgrupo();
        dto.setId(sg.getId());
        dto.setCodigo(sg.getCodigo());
        dto.setCodigoGrupoFk(sg.getPresGrupo() != null ? sg.getPresGrupo().getCodigo() : null);
        dto.setNombre(sg.getNombre());
        dto.setDescripcion(sg.getDescripcion());
        dto.setUsuarioCreacion(sg.getUsuarioCreacion());
        dto.setFechaCreacion(sg.getFechaCreacion());
        dto.setUsuarioModificacion(sg.getUsuarioModificacion());
        dto.setFechaModificacion(sg.getFechaModificacion());
        dto.setEstado(sg.getEstado());
        dto.setAlineacion(sg.getAlineacion());
        return dto;
    }

    private DtoPresGrupo mapPresGrupo(PresGrupo g) {
        if (g == null) return null;
        DtoPresGrupo dto = new DtoPresGrupo();
        dto.setId(g.getId());
        dto.setCodigoNaturalezaFk(g.getPresNaturaleza() != null ? g.getPresNaturaleza().getCodigo() : null);
        dto.setCodigo(g.getCodigo());
        dto.setNombre(g.getNombre());
        dto.setDescripcion(g.getDescripcion());
        dto.setUsuarioCreacion(g.getUsuarioCreacion());
        dto.setFechaCreacion(g.getFechaCreacion());
        dto.setUsuarioModificacion(g.getUsuarioModificacion());
        dto.setFechaModificacion(g.getFechaModificacion());
        dto.setEstado(g.getEstado());
        dto.setAlineacion(g.getAlineacion());
        return dto;
    }

    private DtoPresNaturaleza mapPresNaturaleza(PresNaturaleza n) {
        if (n == null) return null;
        DtoPresNaturaleza dto = new DtoPresNaturaleza();
        dto.setId(n.getId());
        dto.setCodigo(n.getCodigo());
        dto.setNombre(n.getNombre());
        dto.setDescripcion(n.getDescripcion());
        dto.setUsuarioCreacion(n.getUsuarioCreacion());
        dto.setFechaCreacion(n.getFechaCreacion());
        dto.setUsuarioModificacion(n.getUsuarioModificacion());
        dto.setEstado(n.getEstado());
        dto.setAlineacion(n.getAlineacion());
        return dto;
    }
}