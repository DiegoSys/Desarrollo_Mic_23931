package ec.edu.espe.plantillaEspe.service.IService;

import ec.edu.espe.plantillaEspe.dto.DtoEje;
import ec.edu.espe.plantillaEspe.dto.DtoProdInt;
import ec.edu.espe.plantillaEspe.model.ProdInt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceProdInt {
    DtoProdInt find(String codigo);
    Page<DtoProdInt> findAll(Pageable pageable);
    List<DtoProdInt> findAll();
    List<DtoProdInt> findAllActivos();
    Page<DtoProdInt> findAllActivos(Pageable pageable, Map<String, String> searchCriteria);
    DtoProdInt save(DtoProdInt dtoProdInt, String accessToken);
    DtoProdInt update(DtoProdInt dtoProdInt, String accessToken);
    void delete(String codigo, String accessToken);
}