package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.model.ProdInt;
import java.util.List;

public interface IServiceProdInt {
    ProdInt find(String codigo);
    List<ProdInt> findAll();
    ProdInt save(ProdInt objetivo);
    ProdInt update(ProdInt objetivo);
    void delete(String codigo);
}