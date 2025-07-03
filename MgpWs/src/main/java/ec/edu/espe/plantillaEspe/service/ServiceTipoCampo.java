package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoTipoCampo;
import ec.edu.espe.plantillaEspe.dto.DtoTipoCampo;
import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.TipoCampo;
import ec.edu.espe.plantillaEspe.model.Campo;
import ec.edu.espe.plantillaEspe.service.IService.IServiceTipoCampo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceTipoCampo implements IServiceTipoCampo {

    private final DaoTipoCampo daoTipoCampo;
    private final UserInfoService userInfoService;

    @Autowired
    public ServiceTipoCampo(DaoTipoCampo daoTipoCampo, UserInfoService userInfoService) {
        this.daoTipoCampo = daoTipoCampo;
        this.userInfoService = userInfoService;
    }

    @Override
    public DtoTipoCampo find(String codigo) {
        validateCodigo(codigo);
        return daoTipoCampo.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un tipo de campo con el código especificado."));
    }

    @Override
    public List<DtoTipoCampo> findAll() {
        try {
            return daoTipoCampo.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los tipos de campo.", e);
        }
    }

    @Override
    public List<DtoTipoCampo> findAllActivos() {
        try {
            return daoTipoCampo.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los tipos de campo activos.", e);
        }
    }

    @Override
    public Page<DtoTipoCampo> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoTipoCampo.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los tipos de campo paginados.", e);
        }
    }

    @Override
    public Page<DtoTipoCampo> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoTipoCampo.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } else {
            List<DtoTipoCampo> activos = daoTipoCampo.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(activos, searchCriteria, pageable);
        }
    }

    @Override
    @Transactional
    public DtoTipoCampo save(DtoTipoCampo dtoTipoCampo, String accessToken) {
        validateDtoTipoCampo(dtoTipoCampo);

        Optional<TipoCampo> existente = daoTipoCampo.findByCodigo(dtoTipoCampo.getCodigo());
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        if (existente.isPresent()) {
            TipoCampo tipoCampo = existente.get();
            if (Estado.A.equals(tipoCampo.getEstado())) {
                throw new DataValidationException("Ya existe un tipo de campo activo con el código especificado.");
            }
            // Reactivar registro inactivo
            tipoCampo.setEstado(Estado.A);
            tipoCampo.setNombre(dtoTipoCampo.getNombre());
            tipoCampo.setFechaCreacion(new Date());
            tipoCampo.setUsuarioCreacion(username);
            return convertToDto(daoTipoCampo.save(tipoCampo));
        }

        TipoCampo tipoCampo = new TipoCampo();
        tipoCampo.setCodigo(dtoTipoCampo.getCodigo());
        tipoCampo.setNombre(dtoTipoCampo.getNombre());
        tipoCampo.setEstado(Estado.A);
        tipoCampo.setFechaCreacion(new Date());
        tipoCampo.setUsuarioCreacion(username);

        return convertToDto(daoTipoCampo.save(tipoCampo));
    }

    @Override
    @Transactional
    public DtoTipoCampo update(DtoTipoCampo dtoTipoCampo, String accessToken) {
        validateDtoTipoCampo(dtoTipoCampo);

        TipoCampo tipoCampo = daoTipoCampo.findByCodigo(dtoTipoCampo.getCodigo())
                .orElseThrow(() -> new DataValidationException("El tipo de campo con el código especificado no existe."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoTipoCampo.getEstado()) && Estado.A.equals(tipoCampo.getEstado())) {
            throw new DataValidationException("Para desactivar un tipo de campo use el método delete()");
        }

        tipoCampo.setNombre(dtoTipoCampo.getNombre());
        tipoCampo.setEstado(Estado.A);
        tipoCampo.setFechaModificacion(new Date());
        tipoCampo.setUsuarioModificacion(username);

        return convertToDto(daoTipoCampo.save(tipoCampo));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        TipoCampo tipoCampo = daoTipoCampo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un tipo de campo con el código especificado."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        tipoCampo.setEstado(Estado.I);
        tipoCampo.setFechaModificacion(new Date());
        tipoCampo.setUsuarioModificacion(username);

        daoTipoCampo.save(tipoCampo);
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del tipo de campo no puede ser nulo o vacío.");
        }
    }

    private void validateDtoTipoCampo(DtoTipoCampo dtoTipoCampo) {
        if (dtoTipoCampo == null) {
            throw new DataValidationException("DtoTipoCampo no puede ser nulo.");
        }
        validateCodigo(dtoTipoCampo.getCodigo());
        if (dtoTipoCampo.getNombre() == null || dtoTipoCampo.getNombre().isEmpty()) {
            throw new DataValidationException("Nombre es requerido.");
        }
    }

    private DtoTipoCampo convertToDto(TipoCampo tipoCampo) {
        if (tipoCampo == null) {
            return null;
        }
        DtoTipoCampo dto = new DtoTipoCampo();
        dto.setId(tipoCampo.getId());
        dto.setCodigo(tipoCampo.getCodigo());
        dto.setNombre(tipoCampo.getNombre());
        dto.setEstado(tipoCampo.getEstado());
        dto.setFechaCreacion(tipoCampo.getFechaCreacion());
        dto.setUsuarioCreacion(tipoCampo.getUsuarioCreacion());
        dto.setFechaModificacion(tipoCampo.getFechaModificacion());
        dto.setUsuarioModificacion(tipoCampo.getUsuarioModificacion());

        return dto;
    }
}