package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoProdInt;
import ec.edu.espe.plantillaEspe.model.ProdInt;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProdInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceProdInt implements IServiceProdInt {

    @Autowired
    private DaoProdInt daoProdInt;

    @Override
    public ProdInt find(String codigo) {
        return daoProdInt.findById(codigo).orElse(new ProdInt());
    }

    @Override
    public List<ProdInt> findAll() {
        List<ProdInt> prodInts = new ArrayList<>();
        daoProdInt.findAll().forEach(prodInts::add);
        return prodInts;
    }

    @Override
    public ProdInt save(ProdInt producto) {
        producto.setFechaCreacion(new Date());
        producto.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoProdInt.save(producto);
    }

    @Override
    public ProdInt update(ProdInt producto) {
        Optional<ProdInt> productoExistente = daoProdInt.findById(producto.getCodigo());

        if (productoExistente.isPresent()) {
            ProdInt productoActual = productoExistente.get();
            productoActual.setDescripcion(producto.getDescripcion());
            productoActual.setCodigoProginstFk(producto.getCodigoProginstFk());
            productoActual.setFechaModificacion(new Date());
            productoActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoProdInt.save(productoActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoProdInt.deleteById(codigo);
    }

}