package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.PDN;
import java.util.List;

public interface IServicePDN {
    PDN find(String codigo);
    List<PDN> findAll();
    PDN save(PDN objetivo);
    PDN update(PDN objetivo);
    void delete(String codigo);
}