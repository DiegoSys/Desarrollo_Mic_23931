package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.Eje;
import java.util.List;

public interface IServiceEje {
    Eje find(String codigo);
    List<Eje> findAll();
    Eje save(Eje eje);
    Eje update(Eje eje);
    void delete(String codigo);
}