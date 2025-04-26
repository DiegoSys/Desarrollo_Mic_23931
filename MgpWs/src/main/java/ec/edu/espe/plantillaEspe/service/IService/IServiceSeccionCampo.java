package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.SeccionCampo;
import java.util.List;

public interface IServiceSeccionCampo {
    SeccionCampo find(String codigo);
    List<SeccionCampo> findAll();
    SeccionCampo save(SeccionCampo seccionCampo);
    SeccionCampo update(SeccionCampo seccionCampo);
    void delete(String codigo);
}