package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoProgNac;
import ec.edu.espe.plantillaEspe.model.ProgNac;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProgNac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceProgNac implements IServiceProgNac {

    @Autowired
    private DaoProgNac daoProgNac;

    @Override
    public ProgNac find(String codigo) {
        return daoProgNac.findById(codigo).orElse(new ProgNac());
    }

    @Override
    public List<ProgNac> findAll() {
        List<ProgNac> progNacs = new ArrayList<>();
        daoProgNac.findAll().forEach(progNacs::add);
        return progNacs;
    }

    @Override
    public ProgNac save(ProgNac programa) {
        programa.setFechaCreacion(new Date());
        programa.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoProgNac.save(programa);
    }

    @Override
    public ProgNac update(ProgNac programa) {
        Optional<ProgNac> programaExistente = daoProgNac.findById(programa.getCodigo());

        if (programaExistente.isPresent()) {
            ProgNac programaActual = programaExistente.get();
            programaActual.setDescripcion(programa.getDescripcion());
            programaActual.setCodigoMetaFk(programa.getCodigoMetaFk());
            programaActual.setFechaModificacion(new Date());
            programaActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoProgNac.save(programaActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoProgNac.deleteById(codigo);
    }

}