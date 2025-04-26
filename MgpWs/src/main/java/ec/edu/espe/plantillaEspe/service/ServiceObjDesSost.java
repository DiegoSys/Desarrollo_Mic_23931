package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoObjDesSost;
import ec.edu.espe.plantillaEspe.model.ObjDesSost;
import ec.edu.espe.plantillaEspe.service.IService.IServiceObjDesSost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceObjDesSost implements IServiceObjDesSost {

    @Autowired
    private DaoObjDesSost daoObjDesSost;

    @Override
    public ObjDesSost find(String codigo) {
        return daoObjDesSost.findById(codigo).orElse(new ObjDesSost());
    }

    @Override
    public List<ObjDesSost> findAll() {
        List<ObjDesSost> objDesSosts = new ArrayList<>();
        daoObjDesSost.findAll().forEach(objDesSosts::add);
        return objDesSosts;
    }
    @Override
    public ObjDesSost save(ObjDesSost objetivo) {
        objetivo.setEstado("1");
        objetivo.setFechaCreacion(new Date());
        objetivo.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoObjDesSost.save(objetivo);
    }

    @Override
    public ObjDesSost update(ObjDesSost objetivo) {
        Optional<ObjDesSost> objetivoExistente = daoObjDesSost.findById(objetivo.getCodigo());

        if (objetivoExistente.isPresent()) {
            ObjDesSost objetivoActual = objetivoExistente.get();
            objetivoActual.setDescripcion(objetivo.getDescripcion());
            objetivoActual.setFechaModificacion(new Date());
            objetivoActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoObjDesSost.save(objetivoActual);
        }

        return null;
    }



    @Override
    public void delete(String codigo) {
        daoObjDesSost.deleteById(codigo);
    }
}