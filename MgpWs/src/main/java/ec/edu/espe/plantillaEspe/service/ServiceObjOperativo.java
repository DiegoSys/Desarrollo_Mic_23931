package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoObjOperativo;
import ec.edu.espe.plantillaEspe.model.ObjOperativo;
import ec.edu.espe.plantillaEspe.service.IService.IServiceObjOperativo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceObjOperativo implements IServiceObjOperativo {

    @Autowired
    private DaoObjOperativo daoObjOperativo;

    @Override
    public ObjOperativo find(String codigo) {
        return daoObjOperativo.findById(codigo).orElse(new ObjOperativo());
    }

    @Override
    public List<ObjOperativo> findAll() {
        List<ObjOperativo> objOperativos = new ArrayList<>();
        daoObjOperativo.findAll().forEach(objOperativos::add);
        return objOperativos;
    }

    @Override
    public ObjOperativo save(ObjOperativo objetivo) {
        objetivo.setFechaCreacion(new Date());
        objetivo.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoObjOperativo.save(objetivo);
    }

    @Override
    public ObjOperativo update(ObjOperativo objetivo) {
        Optional<ObjOperativo> objetivoExistente = daoObjOperativo.findById(objetivo.getCodigo());

        if (objetivoExistente.isPresent()) {
            ObjOperativo objetivoActual = objetivoExistente.get();
            objetivoActual.setDescripcion(objetivo.getDescripcion());
            objetivoActual.setCodigoEstrFk(objetivo.getCodigoEstrFk());
            objetivoActual.setFechaModificacion(new Date());
            objetivoActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoObjOperativo.save(objetivoActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoObjOperativo.deleteById(codigo);
    }
}