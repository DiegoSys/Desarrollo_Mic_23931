package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoProgInst;
import ec.edu.espe.plantillaEspe.model.ProgInst;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProgInst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceProgInst implements IServiceProgInst {

    @Autowired
    private DaoProgInst daoProgInst;

    @Override
    public ProgInst find(String codigo) {
        return daoProgInst.findById(codigo).orElse(new ProgInst());
    }

    @Override
    public List<ProgInst> findAll() {
        List<ProgInst> progInst = new ArrayList<>();
        daoProgInst.findAll().forEach(progInst::add);
        return progInst;
    }
    @Override
    public ProgInst save(ProgInst programa) {
        programa.setFechaCreacion(new Date());
        programa.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoProgInst.save(programa);
    }

    @Override
    public ProgInst update(ProgInst programa) {
        Optional<ProgInst> programaExistente = daoProgInst.findById(programa.getCodigo());

        if (programaExistente.isPresent()) {
            ProgInst programaActual = programaExistente.get();
            programaActual.setDescripcion(programa.getDescripcion());
            programaActual.setCodigoMetaFk(programa.getCodigoMetaFk());
            programaActual.setFechaModificacion(new Date());
            programaActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoProgInst.save(programaActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoProgInst.deleteById(codigo);
    }

}