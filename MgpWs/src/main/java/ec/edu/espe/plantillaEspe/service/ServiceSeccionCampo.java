package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoSeccionCampo;
import ec.edu.espe.plantillaEspe.model.SeccionCampo;
import ec.edu.espe.plantillaEspe.service.IService.IServiceSeccionCampo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceSeccionCampo implements IServiceSeccionCampo {

    @Autowired
    private DaoSeccionCampo daoSeccionCampo;

    @Override
    public SeccionCampo find(String codigo) {
        return daoSeccionCampo.findById(codigo).orElse(new SeccionCampo());
    }

    @Override
    public List<SeccionCampo> findAll() {
        List<SeccionCampo> seccionesCampos = new ArrayList<>();
        daoSeccionCampo.findAll().forEach(seccionesCampos::add);
        return seccionesCampos;
    }

    @Override
    public SeccionCampo save(SeccionCampo seccionCampo) {
        seccionCampo.setFechaCreacion(new Date());
        seccionCampo.setUsuarioCreacion("ADMIN");
        return daoSeccionCampo.save(seccionCampo);
    }

    @Override
    public SeccionCampo update(SeccionCampo seccionCampo) {
        Optional<SeccionCampo> seccionCampoExistente =
                daoSeccionCampo.findById(seccionCampo.getCodigo());

        if (seccionCampoExistente.isPresent()) {
            SeccionCampo seccionCampoActual = seccionCampoExistente.get();
            seccionCampoActual.setSeccion(seccionCampo.getSeccion());
            seccionCampoActual.setCampo(seccionCampo.getCampo());
            seccionCampoActual.setFechaModificacion(new Date());
            seccionCampoActual.setUsuarioModificacion("ADMIN");

            return daoSeccionCampo.save(seccionCampoActual);
        }
        return null;
    }

    @Override
    public void delete(String codigo) {
        daoSeccionCampo.deleteById(codigo);
    }
}