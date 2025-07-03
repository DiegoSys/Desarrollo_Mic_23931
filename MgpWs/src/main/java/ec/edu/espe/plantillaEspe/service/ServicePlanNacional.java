package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoObjDesSost;
import ec.edu.espe.plantillaEspe.dao.DaoPlanNacional;
import ec.edu.espe.plantillaEspe.dto.DtoPlanNacional;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.model.PDN;
import ec.edu.espe.plantillaEspe.model.PlanNacional;
import ec.edu.espe.plantillaEspe.service.IService.IServicePlanNacional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServicePlanNacional implements IServicePlanNacional {

    private final DaoPlanNacional daoPlanNacional;
    private final UserInfoService userInfoService;
    private final DaoObjDesSost daoObjDesSost;

    @Autowired
    public ServicePlanNacional(DaoPlanNacional daoPlanNacional,
                               DaoObjDesSost daoObjDesSost,
                               UserInfoService userInfoService) {
        this.daoPlanNacional = daoPlanNacional;
        this.userInfoService = userInfoService;
        this.daoObjDesSost = daoObjDesSost;
    }

    @Override
    public DtoPlanNacional find(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código no puede ser nulo o vacío");
        }

        PlanNacional planNacional = daoPlanNacional.findByCodigo(codigo)
                .orElseThrow(() -> new NotFoundException("Plan nacional no encontrado"));

        return mapToDto(planNacional);
    }

    @Override
    public List<DtoPlanNacional> findAll() {
        try {
            return daoPlanNacional.findAll().stream()
                    .map(this::mapToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los planes nacionales.", e);
        }
    }

    @Override
    public List<DtoPlanNacional> findAllActivos() {
        try {
            return daoPlanNacional.findByEstado(Estado.A).stream()
                    .map(this::mapToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los planes nacionales activos.", e);
        }
    }

    @Override
    public Page<DtoPlanNacional> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            try {
                return daoPlanNacional.findByEstado(Estado.A, pageable).map(this::mapToDto);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener los planes nacionales activos.", e);
            }
        } else {
            try {
                List<DtoPlanNacional> planes = daoPlanNacional.findByEstado(Estado.A).stream()
                        .map(this::mapToDto)
                        .toList();
                return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(planes, searchCriteria, pageable);
            } catch (Exception e) {
                throw new RuntimeException("Error al filtrar y paginar los planes nacionales activos.", e);
            }
        }
    }

    @Override
    public Page<DtoPlanNacional> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoPlanNacional.findAll(pageable).map(this::mapToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las planes nacionales.", e);
        }
    }

    @Override
    public DtoPlanNacional save(DtoPlanNacional dtoPlanNacional, String accessToken) {
        validateDto(dtoPlanNacional);

        // Validar que exista el ObjDesSost referenciado
        if (!daoObjDesSost.findByCodigo(dtoPlanNacional.getCodigoObjDessostFk()).isPresent()) {
            throw new DataValidationException("No existe un objetivo de desarrollo sostenible con el código: "
                    + dtoPlanNacional.getCodigoObjDessostFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        // Verificar si existe un registro inactivo con el mismo código
        Optional<PlanNacional> existingInactive = daoPlanNacional.findByCodigo(dtoPlanNacional.getCodigo());
        if (existingInactive.isPresent()) {
            PlanNacional inactivePlan = existingInactive.get();
            if (Estado.I.equals(inactivePlan.getEstado())) {
                // Reactivar el registro existente
                inactivePlan.setCodigoObjDessostFk(dtoPlanNacional.getCodigoObjDessostFk());
                inactivePlan.setDescripcion(dtoPlanNacional.getDescripcion());
                inactivePlan.setEstado(Estado.A);
                inactivePlan.setAlineacion(TipoAlineacion.ESTRATEGICA);
                inactivePlan.setFechaInicio(dtoPlanNacional.getFechaInicio());
                inactivePlan.setFechaFin(dtoPlanNacional.getFechaFin());
                inactivePlan.setUsuarioCreacion(username);
                inactivePlan.setFechaCreacion(new Date());
                
                PlanNacional reactivatedPlan = daoPlanNacional.save(inactivePlan);
                return mapToDto(reactivatedPlan);
            } else {
                throw new DataValidationException("Ya existe un plan nacional activo con el código: " + dtoPlanNacional.getCodigo());
            }
        }

        // Crear nuevo registro
        PlanNacional planNacional = new PlanNacional();
        planNacional.setCodigo(dtoPlanNacional.getCodigo());
        planNacional.setCodigoObjDessostFk(dtoPlanNacional.getCodigoObjDessostFk());
        planNacional.setDescripcion(dtoPlanNacional.getDescripcion());
        planNacional.setEstado(Estado.A);
        planNacional.setAlineacion(TipoAlineacion.ESTRATEGICA);
        planNacional.setFechaInicio(dtoPlanNacional.getFechaInicio());
        planNacional.setFechaFin(dtoPlanNacional.getFechaFin());
        planNacional.setUsuarioCreacion(username);
        planNacional.setFechaCreacion(new Date());

        PlanNacional savedPlan = daoPlanNacional.save(planNacional);
        return mapToDto(savedPlan);
    }


    @Override
    public DtoPlanNacional update(DtoPlanNacional dtoPlanNacional, String accessToken) {
        validateDto(dtoPlanNacional);

        // Buscar el plan nacional por su código propio
        PlanNacional existingPlan = daoPlanNacional.findByCodigo(dtoPlanNacional.getCodigo())
                .orElseThrow(() -> new NotFoundException("Plan nacional no encontrado."));

        // Validar que exista el nuevo ObjDesSost referenciado
        if (!daoObjDesSost.findByCodigo(dtoPlanNacional.getCodigoObjDessostFk()).isPresent()) {
            throw new DataValidationException("No existe un objetivo de desarrollo sostenible con el código: "
                    + dtoPlanNacional.getCodigoObjDessostFk());
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        // Actualizar todos los campos incluyendo codigoObjDessostFk
        existingPlan.setCodigoObjDessostFk(dtoPlanNacional.getCodigoObjDessostFk());
        existingPlan.setDescripcion(dtoPlanNacional.getDescripcion());
        existingPlan.setAlineacion(TipoAlineacion.ESTRATEGICA);
        existingPlan.setEstado(Estado.A);
        existingPlan.setFechaInicio(dtoPlanNacional.getFechaInicio());
        existingPlan.setFechaFin(dtoPlanNacional.getFechaFin());
        existingPlan.setUsuarioModificacion(username);
        existingPlan.setFechaModificacion(new Date());

        validatePlanNacional(existingPlan);
        PlanNacional updatedPlan = daoPlanNacional.save(existingPlan);
        return mapToDto(updatedPlan);
    }
    @Override
    public void delete(String codigo, String accessToken) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código no puede ser nulo o vacío.");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        PlanNacional planNacional = daoPlanNacional.findByCodigo(codigo)
                .orElseThrow(() -> new NotFoundException("Plan nacional no encontrado."));

        planNacional.setEstado(Estado.I);
        planNacional.setFechaModificacion(new Date());
        planNacional.setUsuarioModificacion(username);
        daoPlanNacional.save(planNacional);
    }

    private DtoPlanNacional mapToDto(PlanNacional planNacional) {
        DtoPlanNacional dto = new DtoPlanNacional();
        dto.setId(planNacional.getId());
        dto.setCodigo(planNacional.getCodigo());
        dto.setCodigoObjDessostFk(planNacional.getCodigoObjDessostFk());
        dto.setDescripcion(planNacional.getDescripcion());
        dto.setAlineacion(planNacional.getAlineacion());
        dto.setFechaInicio(planNacional.getFechaInicio());
        dto.setFechaFin(planNacional.getFechaFin());
        dto.setEstado(planNacional.getEstado());
        dto.setUsuarioCreacion(planNacional.getUsuarioCreacion());
        dto.setFechaCreacion(planNacional.getFechaCreacion());
        dto.setUsuarioModificacion(planNacional.getUsuarioModificacion());
        dto.setFechaModificacion(planNacional.getFechaModificacion());
        return dto;
    }

    private void validateDto(DtoPlanNacional dto) {
        if (dto.getCodigo() == null || dto.getCodigo().isEmpty()) {
            throw new DataValidationException("El código del plan nacional es obligatorio.");
        }
        if (dto.getCodigoObjDessostFk() == null || dto.getCodigoObjDessostFk().isEmpty()) {
            throw new DataValidationException("El código del objetivo de desarrollo sostenible es obligatorio.");
        }
        if (dto.getDescripcion() == null || dto.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción es obligatoria.");
        }

    }

    private void validatePlanNacional(PlanNacional planNacional) {
        if (planNacional.getDescripcion() == null || planNacional.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (planNacional.getEstado() == null ) {
            throw new DataValidationException("El estado no puede ser nulo o vacía.");
        }
        if (planNacional.getCodigoObjDessostFk() == null || planNacional.getCodigoObjDessostFk().isEmpty()){
            throw new DataValidationException("El código del objetivo de desarrollo sostenible no puede ser nulo o vacía.");
        }
    }

}
