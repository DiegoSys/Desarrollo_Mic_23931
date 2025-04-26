package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.ProyectoSeccion;
import java.util.List;

public interface IServiceProyectoSeccion {
    ProyectoSeccion find(String codigo);
    List<ProyectoSeccion> findAll();
    ProyectoSeccion save(ProyectoSeccion proyectoSeccion);
    ProyectoSeccion update(ProyectoSeccion proyectoSeccion);
    void delete(String codigo);
}