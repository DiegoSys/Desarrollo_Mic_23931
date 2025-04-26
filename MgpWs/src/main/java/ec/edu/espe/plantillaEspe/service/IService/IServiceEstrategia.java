package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.Estrategia;
import java.util.List;

public interface IServiceEstrategia {
    Estrategia find(String codigo);
    List<Estrategia> findAll();
    Estrategia save(Estrategia estrategia);
    Estrategia update(Estrategia estrategia);
    void delete(String codigo);
}