package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoEstrategia;
import ec.edu.espe.plantillaEspe.model.Estrategia;
import ec.edu.espe.plantillaEspe.service.IService.IServiceEstrategia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceEstrategia implements IServiceEstrategia {

    @Autowired
    private DaoEstrategia daoEstrategia;

    @Override
    public Estrategia find(String codigo) {
        return daoEstrategia.findById(codigo).orElse(new Estrategia());
    }

    @Override
    public List<Estrategia> findAll() {
        List<Estrategia> estrategias = new ArrayList<>();
        daoEstrategia.findAll().forEach(estrategias::add);
        return estrategias;
    }

    @Override
    public Estrategia save(Estrategia estrategia) {
        estrategia.setFechaCreacion(new Date());
        estrategia.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoEstrategia.save(estrategia);
    }

    @Override
    public Estrategia update(Estrategia estrategia) {
        Optional<Estrategia> estrategiaExistente = daoEstrategia.findById(estrategia.getCodigo());

        if (estrategiaExistente.isPresent()) {
            Estrategia estrategiaActual = estrategiaExistente.get();
            estrategiaActual.setDescripcion(estrategia.getDescripcion());
            estrategiaActual.setCodigoPdnFk(estrategia.getCodigoPdnFk());
            estrategiaActual.setFechaModificacion(new Date());
            estrategiaActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoEstrategia.save(estrategiaActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoEstrategia.deleteById(codigo);
    }

}