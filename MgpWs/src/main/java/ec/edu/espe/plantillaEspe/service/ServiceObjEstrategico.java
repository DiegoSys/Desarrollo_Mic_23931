package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoObjEstrategico;
import ec.edu.espe.plantillaEspe.model.ObjEstrategico;
import ec.edu.espe.plantillaEspe.service.IService.IServiceObjEstrategico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceObjEstrategico implements IServiceObjEstrategico {

    @Autowired
    private DaoObjEstrategico daoObjEstrategico;

    @Override
    public ObjEstrategico find(String codigo) {
        return daoObjEstrategico.findById(codigo).orElse(new ObjEstrategico());
    }

    @Override
    public List<ObjEstrategico> findAll() {
        List<ObjEstrategico> objEstrategicos = new ArrayList<>();
        daoObjEstrategico.findAll().forEach(objEstrategicos::add);
        return objEstrategicos;
    }


    @Override
    public ObjEstrategico save(ObjEstrategico objetivo) {
        objetivo.setFechaCreacion(new Date());
        objetivo.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoObjEstrategico.save(objetivo);
    }

    @Override
    public ObjEstrategico update(ObjEstrategico objetivo) {
        Optional<ObjEstrategico> objetivoExistente = daoObjEstrategico.findById(objetivo.getCodigo());

        if (objetivoExistente.isPresent()) {
            ObjEstrategico objetivoActual = objetivoExistente.get();
            objetivoActual.setDescripcion(objetivo.getDescripcion());
            objetivoActual.setCodigoMetaFk(objetivo.getCodigoMetaFk());
            objetivoActual.setFechaModificacion(new Date());
            objetivoActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoObjEstrategico.save(objetivoActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoObjEstrategico.deleteById(codigo);
    }

}