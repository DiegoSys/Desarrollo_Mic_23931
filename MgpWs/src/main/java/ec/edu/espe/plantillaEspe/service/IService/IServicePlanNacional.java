package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.PlanNacional;
import java.util.List;

public interface IServicePlanNacional {
    PlanNacional find(String codigo);
    List<PlanNacional> findAll();
    PlanNacional save(PlanNacional planNacional);
    PlanNacional update(PlanNacional planNacional);
    void delete(String codigo);
}