package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.ObjEstrategico;
import java.util.List;

public interface IServiceObjEstrategico {
    ObjEstrategico find(String codigo);
    List<ObjEstrategico> findAll();
    ObjEstrategico save(ObjEstrategico objetivoEstrategico);
    ObjEstrategico update(ObjEstrategico objetivoEstrategico);
    void delete(String codigo);

}