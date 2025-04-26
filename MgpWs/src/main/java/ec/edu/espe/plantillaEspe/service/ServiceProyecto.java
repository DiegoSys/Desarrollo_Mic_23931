package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoProyecto;
import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import ec.edu.espe.plantillaEspe.model.Proyecto;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProyecto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
public class ServiceProyecto implements IServiceProyecto {

    @Autowired
    private DaoProyecto daoProyecto;

    @Override
    public DtoProyecto find(String codigo) {
        return convertToDto(daoProyecto.findById(codigo).orElse(new Proyecto()));
    }


    @Override
    public Page<DtoProyecto> findAll(Pageable pageable) {
        return daoProyecto.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public DtoProyecto save(DtoProyecto dtoProyecto) {
        Proyecto proyecto = convertToEntity(dtoProyecto);

        proyecto.setFechaCreacion(new Date());
        proyecto.setUsuarioCreacion("ADMIN");
        Proyecto savedProyecto = daoProyecto.save(proyecto);
        return convertToDto(savedProyecto);
    }

    @Override
    public DtoProyecto update(DtoProyecto dtoProyecto) {
        Optional<Proyecto> proyectoExistente = daoProyecto.findById(dtoProyecto.getCodigo());

        if (proyectoExistente.isPresent()) {
            Proyecto proyectoActual = proyectoExistente.get();
            updateEntityFromDto(proyectoActual, dtoProyecto);
            proyectoActual.setFechaModificacion(new Date());
            proyectoActual.setUsuarioModificacion("ADMIN");
            Proyecto updatedProyecto = daoProyecto.save(proyectoActual);
            return convertToDto(updatedProyecto);
        }
        return null;
    }

    @Override
    public void delete(String codigo) {
        daoProyecto.deleteById(codigo);
    }

    private DtoProyecto convertToDto(Proyecto proyecto) {
        DtoProyecto dto = new DtoProyecto();
        dto.setCodigo(proyecto.getCodigo());
        // Establecer otros campos del DTO según sea necesario
        return dto;
    }

    private Proyecto convertToEntity(DtoProyecto dto) {
        Proyecto proyecto = new Proyecto();
        proyecto.setCodigo(dto.getCodigo());
        // Establecer otros campos de la entidad según sea necesario
        return proyecto;
    }

    private void updateEntityFromDto(Proyecto proyecto, DtoProyecto dto) {
        // Actualizar solo los campos necesarios desde el DTO
        if (dto.getCodigo() != null) {
            proyecto.setCodigo(dto.getCodigo());
        }
        // Actualizar otros campos según sea necesario
    }
}