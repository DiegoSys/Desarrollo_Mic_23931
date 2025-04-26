package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.ProgInst;
import java.util.List;

public interface IServiceProgInst {
    ProgInst find(String codigo);
    List<ProgInst> findAll();
    ProgInst save(ProgInst objetivo);
    ProgInst update(ProgInst objetivo);
    void delete(String codigo);

}