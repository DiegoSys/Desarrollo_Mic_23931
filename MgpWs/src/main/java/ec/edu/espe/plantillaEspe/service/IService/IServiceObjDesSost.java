package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.ObjDesSost;
import java.util.List;

public interface IServiceObjDesSost {
    ObjDesSost find(String codigo);
    List<ObjDesSost> findAll();
    ObjDesSost save(ObjDesSost ods);
    ObjDesSost update(ObjDesSost ods);
    void delete(String codigo);

}