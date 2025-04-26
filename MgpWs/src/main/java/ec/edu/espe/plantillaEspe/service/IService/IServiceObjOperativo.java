package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.ObjOperativo;
import java.util.List;

public interface IServiceObjOperativo {
    ObjOperativo find(String codigo);
    List<ObjOperativo> findAll();
    ObjOperativo save(ObjOperativo objetivo);
    ObjOperativo update(ObjOperativo objetivo);
    void delete(String codigo);
}