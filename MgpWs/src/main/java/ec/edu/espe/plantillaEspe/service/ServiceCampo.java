package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.dao.DaoCampo;
import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import ec.edu.espe.plantillaEspe.model.Campo;
import ec.edu.espe.plantillaEspe.service.IService.IServiceCampo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ServiceCampo implements IServiceCampo {

    @Autowired
    private DaoCampo daoCampo;

    @Override
    public DtoCampo find(String codigo) {
        return convertToDto(daoCampo.findById(codigo).orElse(new Campo()));
    }

    @Override
    public Page<DtoCampo> findAll(Pageable pageable) {
        return daoCampo.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public DtoCampo save(DtoCampo dtoCampo) {
        Campo campo = convertToEntity(dtoCampo);
        campo.setEstado("1");
        campo.setFechaCreacion(new Date());
        campo.setUsuarioCreacion("ADMIN");
        return convertToDto(daoCampo.save(campo));
    }

    @Override
    public DtoCampo update(DtoCampo dtoCampo) {
        Optional<Campo> campoExistente = daoCampo.findById(dtoCampo.getCodigo());
        if (!campoExistente.isPresent()) {
            return null;
        }

        Campo campoActual = campoExistente.get();
        updateEntityFromDto(campoActual, dtoCampo);
        campoActual.setFechaModificacion(new Date());
        campoActual.setUsuarioModificacion("ADMIN");

        return convertToDto(daoCampo.save(campoActual));
    }

    @Override
    public void delete(String codigo) {
        Optional<Campo> campo = daoCampo.findById(codigo);
        if (campo.isPresent()) {
            daoCampo.deleteById(codigo);
        }
    }

    private DtoCampo convertToDto(Campo campo) {
        DtoCampo dto = new DtoCampo();
        dto.setCodigo(campo.getCodigo());
        dto.setDescripcion(campo.getDescripcion());
        dto.setEstado(campo.getEstado());
        return dto;
    }

    private Campo convertToEntity(DtoCampo dto) {
        Campo campo = new Campo();
        campo.setCodigo(dto.getCodigo());
        campo.setDescripcion(dto.getDescripcion());
        return campo;
    }

    private void updateEntityFromDto(Campo campo, DtoCampo dto) {
        if (dto.getDescripcion() != null) {
            campo.setDescripcion(dto.getDescripcion());
        }
        if (dto.getEstado() != null) {
            campo.setEstado(dto.getEstado());
        }
    }
}