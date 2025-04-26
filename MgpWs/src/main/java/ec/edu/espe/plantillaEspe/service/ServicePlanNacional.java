package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoPlanNacional;
import ec.edu.espe.plantillaEspe.model.PlanNacional;
import ec.edu.espe.plantillaEspe.service.IService.IServicePlanNacional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServicePlanNacional implements IServicePlanNacional {

    @Autowired
    private DaoPlanNacional daoPlanNacional;

    @Override
    public PlanNacional find(String codigo) {
        return daoPlanNacional.findById(codigo).orElse(new PlanNacional());
    }

    @Override
    public List<PlanNacional> findAll() {
        List<PlanNacional> planNacionals = new ArrayList<>();
        daoPlanNacional.findAll().forEach(planNacionals::add);
        return planNacionals;
    }
    @Override
    public PlanNacional save(PlanNacional plan) {
        plan.setFechaCreacion(new Date());
        plan.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoPlanNacional.save(plan);
    }

    @Override
    public PlanNacional update(PlanNacional plan) {
        Optional<PlanNacional> planExistente = daoPlanNacional.findById(plan.getCodigo());

        if (planExistente.isPresent()) {
            PlanNacional planActual = planExistente.get();
            planActual.setDescripcion(plan.getDescripcion());
            planActual.setCodigoObjDessostFk(plan.getCodigoObjDessostFk());
            planActual.setEstado(plan.getEstado());
            planActual.setFechaInicio(plan.getFechaInicio());
            planActual.setFechaFin(plan.getFechaFin());
            planActual.setFechaModificacion(new Date());
            planActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoPlanNacional.save(planActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoPlanNacional.deleteById(codigo);
    }

}