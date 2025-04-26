package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoProyectoSeccion;
import ec.edu.espe.plantillaEspe.model.ProyectoSeccion;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProyectoSeccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceProyectoSeccion implements IServiceProyectoSeccion {

    @Autowired
    private DaoProyectoSeccion daoProyectoSeccion;

    @Override
    public ProyectoSeccion find(String codigo) {
        return daoProyectoSeccion.findById(codigo).orElse(new ProyectoSeccion());
    }

    @Override
    public List<ProyectoSeccion> findAll() {
        List<ProyectoSeccion> proyectoSecciones = new ArrayList<>();
        daoProyectoSeccion.findAll().forEach(proyectoSecciones::add);
        return proyectoSecciones;
    }

    @Override
    public ProyectoSeccion save(ProyectoSeccion proyectoSeccion) {
        proyectoSeccion.setFechaCreacion(new Date());
        proyectoSeccion.setUsuarioCreacion("ADMIN");
        return daoProyectoSeccion.save(proyectoSeccion);
    }

    @Override
    public ProyectoSeccion update(ProyectoSeccion proyectoSeccion) {
        Optional<ProyectoSeccion> proyectoSeccionExistente =
                daoProyectoSeccion.findById(proyectoSeccion.getCodigo());

        if (proyectoSeccionExistente.isPresent()) {
            ProyectoSeccion proyectoSeccionActual = proyectoSeccionExistente.get();
            proyectoSeccionActual.setProyecto(proyectoSeccion.getProyecto());
            proyectoSeccionActual.setSeccion(proyectoSeccion.getSeccion());
            proyectoSeccionActual.setFechaModificacion(new Date());
            proyectoSeccionActual.setUsuarioModificacion("ADMIN");

            return daoProyectoSeccion.save(proyectoSeccionActual);
        }
        return null;
    }

    @Override
    public void delete(String codigo) {
        daoProyectoSeccion.deleteById(codigo);
    }
}