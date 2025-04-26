package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoPDN;
import ec.edu.espe.plantillaEspe.model.PDN;
import ec.edu.espe.plantillaEspe.service.IService.IServicePDN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServicePDN implements IServicePDN {

    @Autowired
    private DaoPDN daoPDN;

    @Override
    public PDN find(String codigo) {
        return daoPDN.findById(codigo).orElse(new PDN());
    }

    @Override
    public List<PDN> findAll() {
        List<PDN> pdns = new ArrayList<>();
        daoPDN.findAll().forEach(pdns::add);
        return pdns;
    }

    @Override
    public PDN save(PDN politica) {
        politica.setFechaCreacion(new Date());
        politica.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoPDN.save(politica);
    }

    @Override
    public PDN update(PDN politica) {
        Optional<PDN> politicaExistente = daoPDN.findById(politica.getCodigo());

        if (politicaExistente.isPresent()) {
            PDN politicaActual = politicaExistente.get();
            politicaActual.setDescripcion(politica.getDescripcion());
            politicaActual.setCodigoOpnFk(politica.getCodigoOpnFk());
            politicaActual.setFechaModificacion(new Date());
            politicaActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoPDN.save(politicaActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoPDN.deleteById(codigo);
    }
}