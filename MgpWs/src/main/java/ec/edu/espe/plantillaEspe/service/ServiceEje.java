package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoEje;
import ec.edu.espe.plantillaEspe.dao.DaoPlanNacional;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.model.PlanNacional;
import ec.edu.espe.plantillaEspe.dto.DtoEje;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Eje;
import ec.edu.espe.plantillaEspe.service.IService.IServiceEje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceEje implements IServiceEje {

    private final DaoEje daoEje;
    private final UserInfoService userInfoService;
    private final DaoPlanNacional daoPlanNacional;

    @Autowired
    public ServiceEje(DaoEje daoEje, 
                     UserInfoService userInfoService,
                     DaoPlanNacional daoPlanNacional) {
        this.daoEje = daoEje;
        this.userInfoService = userInfoService;
        this.daoPlanNacional = daoPlanNacional;
    }

    @Override
    public DtoEje find(String codigo) {
        validateCodigo(codigo);
        return daoEje.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un eje con el código especificado."));
    }

    @Override
    public List<DtoEje> findAll() {
        try {
            return daoEje.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los Ejes.", e);
        }
    }

    @Override
    public List<DtoEje> findAllActivos() {
        try {
            return daoEje.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los ejes activos.", e);
        }
    }

    @Override
    public Page<DtoEje> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoEje.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los ejes paginados.", e);
        }
    }

    @Override
    public Page<DtoEje> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            try {
                return daoEje.findByEstado(Estado.A, pageable).map(this::convertToDto);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener los ejes paginados.", e);
            }
        } else {
            try {
                List<DtoEje> ejes = daoEje.findByEstado(Estado.A).stream()
                        .map(this::convertToDto)
                        .toList();
                return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(ejes, searchCriteria, pageable);
            } catch (Exception e) {
                throw new RuntimeException("Error al filtrar y paginar los ejes activos.", e);
            }
        }
    }
    

    @Override
    public DtoEje save(DtoEje dtoEje, String accessToken) {
        validateDtoEje(dtoEje);

        Optional<Eje> ejeExistente = daoEje.findByCodigo(dtoEje.getCodigo());
        if (ejeExistente.isPresent()) {
            Eje eje = ejeExistente.get();
            if (Estado.A.equals(eje.getEstado())) {
                throw new DataValidationException("Ya existe un eje activo con el código especificado.");
            } else {
                // Reactivar registro inactivo
                eje.setEstado(Estado.A);
                eje.setDescripcion(dtoEje.getDescripcion());
                eje.setAlineacion(TipoAlineacion.ESTRATEGICA);
                eje.setCodigoPlanNacionalFk(dtoEje.getCodigoPlanNacionalFk());
                eje.setFechaCreacion(new Date());
                Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
                String username = (String) userInfo.get("name");
                eje.setUsuarioCreacion(username);
                return convertToDto(daoEje.save(eje));
            }
        }

        // Validar que exista el Plan Nacional referenciado
        if (!daoPlanNacional.findByCodigo(dtoEje.getCodigoPlanNacionalFk()).isPresent()) {
            throw new DataValidationException("No existe un plan nacional con el código: " + dtoEje.getCodigoPlanNacionalFk());
        }

        // Crear nuevo registro
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        Eje eje = new Eje();
        eje.setCodigo(dtoEje.getCodigo());
        eje.setDescripcion(dtoEje.getDescripcion());
        eje.setEstado(Estado.A); // Nuevo registro siempre activo
        eje.setAlineacion(TipoAlineacion.ESTRATEGICA);
        eje.setCodigoPlanNacionalFk(dtoEje.getCodigoPlanNacionalFk());
        eje.setFechaCreacion(new Date());
        eje.setUsuarioCreacion(username);

        return convertToDto(daoEje.save(eje));
    }


    @Override
    public DtoEje update(DtoEje dtoEje, String accessToken) {
        validateDtoEje(dtoEje);

        Eje ejeActual = daoEje.findByCodigo(dtoEje.getCodigo())
                .orElseThrow(() -> new DataValidationException("El eje con el código especificado no existe."));

        // Validar que exista el Plan Nacional referenciado
        if (!daoPlanNacional.findByCodigo(dtoEje.getCodigoPlanNacionalFk()).isPresent()) {
            throw new DataValidationException("No existe un plan nacional con el código: " + dtoEje.getCodigoPlanNacionalFk());
        }

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoEje.getEstado()) && Estado.A.equals(ejeActual.getEstado())) {
            throw new DataValidationException("Para desactivar un eje use el método delete()");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        ejeActual.setDescripcion(dtoEje.getDescripcion());
        ejeActual.setCodigoPlanNacionalFk(dtoEje.getCodigoPlanNacionalFk());
        ejeActual.setEstado(Estado.A);
        ejeActual.setAlineacion(TipoAlineacion.ESTRATEGICA);
        ejeActual.setFechaModificacion(new Date());
        ejeActual.setAlineacion(dtoEje.getAlineacion());
        ejeActual.setUsuarioModificacion(username);

        validateEje(ejeActual);
        return convertToDto(daoEje.save(ejeActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código no puede ser nulo o vacío.");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        Eje eje = daoEje.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un eje con el código especificado."));
        eje.setEstado(Estado.I);
        eje.setUsuarioModificacion(username);
        eje.setFechaModificacion(new Date());
        daoEje.save(eje);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del eje no puede ser nulo o vacío.");
        }
    }

    private void validateDtoEje(DtoEje dtoEje) {
        if (dtoEje == null) {
            throw new DataValidationException("DtoEje no puede ser nulo.");
        }
        validateCodigo(dtoEje.getCodigo());
        if (dtoEje.getDescripcion() == null || dtoEje.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida.");
        }
        if (dtoEje.getCodigoPlanNacionalFk() == null || dtoEje.getCodigoPlanNacionalFk().isEmpty()) {
            throw new DataValidationException("Código de Plan Nacional es .");
        }
    }

    private void validateEje(Eje eje) {
        if (eje.getDescripcion() == null || eje.getDescripcion().isEmpty()) {
            throw new DataValidationException("La descripción no puede ser nula o vacía.");
        }
        if (eje.getEstado() == null) {
            throw new DataValidationException("El estado no puede ser nulo.");
        }
        if (!(eje.getEstado() instanceof Estado)) {
            throw new DataValidationException("Estado debe ser un valor válido (A/I)");
        }
        if (eje.getCodigoPlanNacionalFk() == null || eje.getCodigoPlanNacionalFk().isEmpty()) {
            throw new DataValidationException("El código de Plan Nacional no puede ser nulo o vacío.");
        }
    }

    private DtoEje convertToDto(Eje eje) {
        if (eje == null) {
            return null;
        }
        DtoEje dto = new DtoEje();
        dto.setId(eje.getId());
        dto.setCodigo(eje.getCodigo());
        dto.setEstado(eje.getEstado());
        dto.setDescripcion(eje.getDescripcion());
        dto.setCodigoPlanNacionalFk(eje.getCodigoPlanNacionalFk());
        dto.setAlineacion(eje.getAlineacion());
        dto.setFechaCreacion(eje.getFechaCreacion());
        dto.setUsuarioCreacion(eje.getUsuarioCreacion());
        dto.setFechaModificacion(eje.getFechaModificacion());
        dto.setUsuarioModificacion(eje.getUsuarioModificacion());
        return dto;
    }
}
