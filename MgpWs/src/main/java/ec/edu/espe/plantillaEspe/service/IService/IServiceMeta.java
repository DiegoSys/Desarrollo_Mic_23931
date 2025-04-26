package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.Meta;
import java.util.List;

public interface IServiceMeta {
    Meta find(String codigo);
    List<Meta> findAll();
    Meta save(Meta meta);
    Meta update(Meta meta);
    void delete(String codigo);
}