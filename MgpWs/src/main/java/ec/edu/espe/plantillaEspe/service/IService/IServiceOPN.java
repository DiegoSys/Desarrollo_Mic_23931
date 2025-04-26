package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.OPN;
import java.util.List;

public interface IServiceOPN {
    OPN find(String codigo);
    List<OPN> findAll();
    OPN save(OPN opn);
    OPN update(OPN opn);
    void delete(String codigo);
}