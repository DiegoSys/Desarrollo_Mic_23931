package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoMeta;
import ec.edu.espe.plantillaEspe.model.Meta;
import ec.edu.espe.plantillaEspe.service.IService.IServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceMeta implements IServiceMeta {

    @Autowired
    private DaoMeta daoMeta;

    @Override
    public Meta find(String codigo) {
        return daoMeta.findById(codigo).orElse(new Meta());
    }

    @Override
    public List<Meta> findAll() {
        List<Meta> metas = new ArrayList<>();
        daoMeta.findAll().forEach(metas::add);
        return metas;
    }


    @Override
    public Meta save(Meta meta) {
        meta.setFechaCreacion(new Date());
        meta.setUsuarioCreacion("ADMIN"); // Considera obtener el usuario real del sistema
        return daoMeta.save(meta);
    }

    @Override
    public Meta update(Meta meta) {
        Optional<Meta> metaExistente = daoMeta.findById(meta.getCodigo());

        if (metaExistente.isPresent()) {
            Meta metaActual = metaExistente.get();
            metaActual.setDescripcion(meta.getDescripcion());
            metaActual.setCodigoOpnFk(meta.getCodigoOpnFk());
            metaActual.setFechaModificacion(new Date());
            metaActual.setUsuarioModificacion("ADMIN"); // Considera obtener el usuario real del sistema

            return daoMeta.save(metaActual);
        }

        return null;
    }

    @Override
    public void delete(String codigo) {
        daoMeta.deleteById(codigo);
    }

}