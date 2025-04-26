package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.ProgNac;
import java.util.List;

public interface IServiceProgNac {
    ProgNac find(String codigo);
    List<ProgNac> findAll();
    ProgNac save(ProgNac objetivo);
    ProgNac update(ProgNac objetivo);
    void delete(String codigo);
}