package ec.edu.espe.plantillaEspe.service;

import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoCampo;
import ec.edu.espe.plantillaEspe.dao.DaoTipoCampo;
import ec.edu.espe.plantillaEspe.dao.DaoMatriz;
import ec.edu.espe.plantillaEspe.dao.DaoSeccionCampo;
import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Campo;
import ec.edu.espe.plantillaEspe.model.TipoCampo;
import ec.edu.espe.plantillaEspe.model.Matriz;
import ec.edu.espe.plantillaEspe.service.IService.IServiceCampo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceCampo implements IServiceCampo {

    private final DaoCampo daoCampo;
    private final DaoTipoCampo daoTipoCampo;
    private final DaoMatriz daoMatriz;
    private final UserInfoService userInfoService;
    private final DaoSeccionCampo daoSeccionCampo;

    @Autowired
    public ServiceCampo(DaoCampo daoCampo, DaoTipoCampo daoTipoCampo, DaoMatriz daoMatriz, UserInfoService userInfoService,
                        DaoSeccionCampo daoSeccionCampo) {
        this.daoCampo = daoCampo;
        this.daoTipoCampo = daoTipoCampo;
        this.daoMatriz = daoMatriz;
        this.userInfoService = userInfoService;
        this.daoSeccionCampo = daoSeccionCampo;
    }

    @Override
    public DtoCampo find(String codigo) {
        validateCodigo(codigo);
        return daoCampo.findByCodigo(codigo)
                .map(this::convertToDto)
                .orElseThrow(() -> new DataValidationException("No se encontró un campo con el código especificado."));
    }

    @Override
    public List<DtoCampo> findAll() {
        try {
            return daoCampo.findAll().stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los campos.", e);
        }
    }

    @Override
    public List<DtoCampo> findAllActivos() {
        try {
            return daoCampo.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los campos activos.", e);
        }
    }

    @Override
    public Page<DtoCampo> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        try {
            return daoCampo.findAll(pageable).map(this::convertToDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los campos paginados.", e);
        }
    }

    @Override
    public Page<DtoCampo> findAllActivos(Pageable pageable, Map<String, String> searchCriteria) {
        if (pageable == null) {
            throw new DataValidationException("Los parámetros de paginación son requeridos.");
        }
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return daoCampo.findByEstado(Estado.A, pageable).map(this::convertToDto);
        } else {
            List<DtoCampo> activos = daoCampo.findByEstado(Estado.A).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ec.edu.espe.plantillaEspe.util.GenericSearchUtil.search(activos, searchCriteria, pageable);
        }
    }

        @Override
    @Transactional
    public DtoCampo save(DtoCampo dtoCampo, String accessToken) {
        validateDtoCampo(dtoCampo);
    
        // Validar existencia de campo con el mismo código
        Optional<Campo> existente = daoCampo.findByCodigo(dtoCampo.getCodigo());
        if (existente.isPresent() && Estado.A.equals(existente.get().getEstado())) {
            throw new DataValidationException("Ya existe un campo activo con el código especificado.");
        }
    
        // Validar existencia de tipoCampo
        TipoCampo tipoCampo = null;
        if (dtoCampo.getTipoCampo() != null && !dtoCampo.getTipoCampo().isEmpty()) {
            tipoCampo = daoTipoCampo.findByCodigo(dtoCampo.getTipoCampo())
                    .orElseThrow(() -> new DataValidationException("TipoCampo no encontrado: " + dtoCampo.getTipoCampo()));
        }
    
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");
    
        // Si es reactivación, actualizar el campo existente
        if (existente.isPresent()) {
            Campo campo = existente.get();
            campo.setEstado(Estado.A);
            campo.setNombre(dtoCampo.getNombre());
            campo.setDescripcion(dtoCampo.getDescripcion());
            campo.setFechaCreacion(new Date());
            campo.setUsuarioCreacion(username);
            campo.setTipoCampo(tipoCampo);
    
            // Crear la matriz solo cuando todo es válido
            Matriz matriz = new Matriz();
            matriz.setFila(dtoCampo.getFilaMatriz());
            matriz.setColumna(dtoCampo.getColumnaMatriz());
            matriz.setFechaCreacion(new Date());
            matriz.setUsuarioCreacion(username);
            matriz = daoMatriz.save(matriz);
    
            campo.setMatriz(matriz);
            return convertToDto(daoCampo.save(campo));
        }
    
        // Crear la matriz solo cuando todo es válido
        Matriz matriz = new Matriz();
        matriz.setFila(dtoCampo.getFilaMatriz());
        matriz.setColumna(dtoCampo.getColumnaMatriz());
        matriz.setFechaCreacion(new Date());
        matriz.setUsuarioCreacion(username);
        matriz = daoMatriz.save(matriz);
    
        Campo campo = new Campo();
        campo.setCodigo(dtoCampo.getCodigo());
        campo.setNombre(dtoCampo.getNombre());
        campo.setDescripcion(dtoCampo.getDescripcion());
        campo.setEstado(Estado.A);
        campo.setFechaCreacion(new Date());
        campo.setUsuarioCreacion(username);
        campo.setTipoCampo(tipoCampo);
        campo.setMatriz(matriz);
    
        return convertToDto(daoCampo.save(campo));
    }

    @Override
    @Transactional
    public DtoCampo update(DtoCampo dtoCampo, String accessToken) {
        validateDtoCampo(dtoCampo);
        Campo campoActual = daoCampo.findByCodigo(dtoCampo.getCodigo())
                .orElseThrow(() -> new DataValidationException("El campo con el código especificado no existe."));

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        // No permitir cambiar estado a inactivo desde update (usar delete para eso)
        if (Estado.I.equals(dtoCampo.getEstado()) && Estado.A.equals(campoActual.getEstado())) {
            throw new DataValidationException("Para desactivar un campo use el método delete()");
        }

        campoActual.setNombre(dtoCampo.getNombre());
        campoActual.setDescripcion(dtoCampo.getDescripcion());
        campoActual.setEstado(Estado.A);
        campoActual.setFechaModificacion(new Date());
        campoActual.setUsuarioModificacion(username);

        TipoCampo tipoCampo = null;
        if (dtoCampo.getTipoCampo() != null && !dtoCampo.getTipoCampo().isEmpty()) {
            tipoCampo = daoTipoCampo.findByCodigo(dtoCampo.getTipoCampo())
                    .orElseThrow(() -> new DataValidationException("TipoCampo no encontrado: " + dtoCampo.getTipoCampo()));
        }
        campoActual.setTipoCampo(tipoCampo);

        // Actualizar la matriz asociada
        Matriz matriz = campoActual.getMatriz();
        if (matriz == null) {
            matriz = new Matriz();
            matriz.setFechaCreacion(new Date());
            matriz.setUsuarioCreacion(username);
        }
        matriz.setFila(dtoCampo.getFilaMatriz());
        matriz.setColumna(dtoCampo.getColumnaMatriz());
        matriz.setFechaModificacion(new Date());
        matriz.setUsuarioModificacion(username);
        matriz = daoMatriz.save(matriz);

        campoActual.setMatriz(matriz);

        return convertToDto(daoCampo.save(campoActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        Campo campo = daoCampo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un campo con el código especificado."));
    
        // Eliminar hijos SeccionCampo en la base de datos
        daoSeccionCampo.deleteByCampo(campo);
    
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");
    
        campo.setEstado(Estado.I);
        campo.setFechaModificacion(new Date());
        campo.setUsuarioModificacion(username);
    
        // Desasociar la matriz antes de eliminarla
        Matriz matriz = campo.getMatriz();
        campo.setMatriz(null);
        daoCampo.save(campo); // Guarda el campo sin la matriz asociada
    
        // Ahora sí elimina la matriz si existe
        if (matriz != null) {
            daoMatriz.delete(matriz);
        }
    }

    private void validateCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new DataValidationException("El código del campo no puede ser nulo o vacío.");
        }
    }

    private void validateDtoCampo(DtoCampo dtoCampo) {
        if (dtoCampo == null) {
            throw new DataValidationException("DtoCampo no puede ser nulo");
        }
        validateCodigo(dtoCampo.getCodigo());
        if (dtoCampo.getNombre() == null || dtoCampo.getNombre().isEmpty()) {
            throw new DataValidationException("Nombre es requerido.");
        }
        if (dtoCampo.getDescripcion() == null || dtoCampo.getDescripcion().isEmpty()) {
            throw new DataValidationException("Descripción es requerida");
        }
        if (dtoCampo.getTipoCampo() == null || dtoCampo.getTipoCampo().isEmpty()) {
            throw new DataValidationException("El tipoCampo es requerido");
        }
        if (dtoCampo.getFilaMatriz() == null) {
            throw new DataValidationException("Fila de la matriz es requerida");
        }
        if (dtoCampo.getColumnaMatriz() == null) {
            throw new DataValidationException("Columna de la matriz es requerida");
        }
    }

    private DtoCampo convertToDto(Campo campo) {
        if (campo == null) {
            return null;
        }
        DtoCampo dto = new DtoCampo();
        dto.setId(campo.getId());
        dto.setCodigo(campo.getCodigo());
        dto.setNombre(campo.getNombre());
        dto.setDescripcion(campo.getDescripcion());
        dto.setEstado(campo.getEstado());
        dto.setFechaCreacion(campo.getFechaCreacion());
        dto.setUsuarioCreacion(campo.getUsuarioCreacion());
        dto.setFechaModificacion(campo.getFechaModificacion());
        dto.setUsuarioModificacion(campo.getUsuarioModificacion());

        if (campo.getTipoCampo() != null) {
            dto.setTipoCampo(campo.getTipoCampo().getCodigo());
        } else {
            dto.setTipoCampo(null);
        }

        if (campo.getMatriz() != null) {
            dto.setFilaMatriz(campo.getMatriz().getFila());
            dto.setColumnaMatriz(campo.getMatriz().getColumna());
        } else {
            dto.setFilaMatriz(null);
            dto.setColumnaMatriz(null);
        }
        return dto;
    }
}