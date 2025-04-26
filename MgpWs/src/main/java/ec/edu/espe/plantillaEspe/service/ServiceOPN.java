package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoOPN;
import ec.edu.espe.plantillaEspe.model.OPN;
import ec.edu.espe.plantillaEspe.service.IService.IServiceOPN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceOPN implements IServiceOPN {

    @Autowired
    private DaoOPN daoOPN;

    @Override
    public OPN find(String codigo) {
        return daoOPN.findById(codigo).orElse(new OPN());
    }

    @Override
    public List<OPN> findAll() {
        List<OPN> opns = new ArrayList<>();
        daoOPN.findAll().forEach(opns::add);
        return opns;
    }

    @Override
    public OPN save(OPN objetivo) {
        objetivo.setFechaCreacion(new Date());
        objetivo.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoOPN.save(objetivo);
    }

    @Override
    public OPN update(OPN objetivo) {
        Optional<OPN> objetivoExistente = daoOPN.findById(objetivo.getCodigo());

        if (objetivoExistente.isPresent()) {
            OPN objetivoActual = objetivoExistente.get();
            objetivoActual.setDescripcion(objetivo.getDescripcion());
            objetivoActual.setCodigoPlanNacionalFk(objetivo.getCodigoPlanNacionalFk());
            objetivoActual.setFechaModificacion(new Date());
            objetivoActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoOPN.save(objetivoActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoOPN.deleteById(codigo);
    }

}