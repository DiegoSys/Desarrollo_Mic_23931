package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoEje;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.service.IService.IServiceEje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceEje implements IServiceEje {

    @Autowired
    private DaoEje daoEje;

    @Override
    public Eje find(String codigo) {
        return daoEje.findById(codigo).orElse(new Eje());
    }


    @Override
    public Eje update(Eje eje) {
        Optional<Eje> ejeExistente = daoEje.findById(eje.getCodigo());

        if (ejeExistente.isPresent()) {
            Eje ejeActual = ejeExistente.get();
            ejeActual.setDescripcion(eje.getDescripcion());
            ejeActual.setCodigoPlanNacionalFk(eje.getCodigoPlanNacionalFk());
            ejeActual.setFechaModificacion(new Date());
            ejeActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoEje.save(ejeActual);
        }

        return null;
    }

    @Override
    public Eje save(Eje eje) {
        eje.setFechaCreacion(new Date());
        eje.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoEje.save(eje);
    }

    @Override
    public void delete(String codigo) {
        daoEje.deleteById(codigo);
    }

    @Override
    public List<Eje> findAll() {
        List<Eje> ejes = new ArrayList<>();
        daoEje.findAll().forEach(ejes::add);
        return ejes;
    }
}