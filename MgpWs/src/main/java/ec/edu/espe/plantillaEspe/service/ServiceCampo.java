package ec.edu.espe.plantillaEspe.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.plantillaEspe.config.security.UserInfoService;
import ec.edu.espe.plantillaEspe.dao.DaoCampo;
import ec.edu.espe.plantillaEspe.dao.DaoTipoCampo;
import ec.edu.espe.plantillaEspe.dao.DaoSeccionCampo;
import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoConfiguracionTabla;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.model.Campo;
import ec.edu.espe.plantillaEspe.model.TipoCampo;
import ec.edu.espe.plantillaEspe.model.SeccionCampo;
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
    private final UserInfoService userInfoService;
    private final DaoSeccionCampo daoSeccionCampo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ServiceCampo(DaoCampo daoCampo, DaoTipoCampo daoTipoCampo, UserInfoService userInfoService,
                        DaoSeccionCampo daoSeccionCampo) {
        this.daoCampo = daoCampo;
        this.daoTipoCampo = daoTipoCampo;
        this.userInfoService = userInfoService;
        this.daoSeccionCampo = daoSeccionCampo;
    }

    private String generarCodigoUnico() {
        List<String> codigos = daoCampo.findAllCodigosLikeCam();
        int max = 0;
        for (String codigo : codigos) {
            if (codigo != null && codigo.startsWith("CAM-")) {
                String numStr = codigo.substring(4).replaceFirst("^0+(?!$)", "");
                try {
                    int num = Integer.parseInt(numStr);
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {
                    // ignora códigos basura
                }
            }
        }
        int siguienteNumero = max + 1;
        String nuevoCodigo;
        do {
            nuevoCodigo = "CAM-" + siguienteNumero++;
        } while (daoCampo.existsByCodigo(nuevoCodigo));
        return nuevoCodigo;
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

        dtoCampo.setCodigo(generarCodigoUnico());

        Optional<Campo> existente = daoCampo.findByCodigo(dtoCampo.getCodigo());
        if (existente.isPresent() && Estado.A.equals(existente.get().getEstado())) {
            throw new DataValidationException("Ya existe un campo activo con el código especificado.");
        }

        TipoCampo tipoCampo = null;
        if (dtoCampo.getTipoCampo() != null && !dtoCampo.getTipoCampo().isEmpty()) {
            tipoCampo = daoTipoCampo.findByCodigo(dtoCampo.getTipoCampo())
                    .orElseThrow(() -> new DataValidationException("TipoCampo no encontrado: " + dtoCampo.getTipoCampo()));
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        // Serializar directamente los objetos
        String opcionesJson = toJsonFromObjects(dtoCampo.getOpciones());
        String columnasJson = toJsonFromObjects(dtoCampo.getColumnas());
        String filasJson = toJsonFromObjects(dtoCampo.getFilas());
        String estructuraJson = toJsonFromObject(dtoCampo.getEstructuraTablaPersonalizada());

        Campo campo = new Campo();
        campo.setCodigo(dtoCampo.getCodigo());
        campo.setLabel(dtoCampo.getLabel());
        campo.setTipoCampo(tipoCampo);
        campo.setRequerido(dtoCampo.getRequerido());
        campo.setSoloLectura(dtoCampo.getSoloLectura());
        campo.setEsMultiple(dtoCampo.getEsMultiple());
        campo.setOpciones(opcionesJson);
        campo.setColumnas(columnasJson);
        campo.setFilas(filasJson);
        campo.setEstructuraTablaPersonalizada(estructuraJson);
        campo.setTipoConfiguracionTabla(dtoCampo.getTipoConfiguracionTabla());
        campo.setEsMutable(dtoCampo.getEsMutable());
        campo.setMinFilas(dtoCampo.getMinFilas());
        campo.setMaxFilas(dtoCampo.getMaxFilas());
        campo.setMinColumnas(dtoCampo.getMinColumnas());
        campo.setMaxColumnas(dtoCampo.getMaxColumnas());
        campo.setEstado(Estado.A);
        campo.setFechaCreacion(new Date());
        campo.setUsuarioCreacion(username);

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

        campoActual.setLabel(dtoCampo.getLabel());
        campoActual.setRequerido(dtoCampo.getRequerido());
        campoActual.setSoloLectura(dtoCampo.getSoloLectura());
        campoActual.setEsMultiple(dtoCampo.getEsMultiple());
        campoActual.setOpciones(toJsonFromObjects(dtoCampo.getOpciones()));
        campoActual.setColumnas(toJsonFromObjects(dtoCampo.getColumnas()));
        campoActual.setFilas(toJsonFromObjects(dtoCampo.getFilas()));
        campoActual.setEstructuraTablaPersonalizada(toJsonFromObject(dtoCampo.getEstructuraTablaPersonalizada()));
        campoActual.setTipoConfiguracionTabla(dtoCampo.getTipoConfiguracionTabla());
        campoActual.setEsMutable(dtoCampo.getEsMutable());
        campoActual.setMinFilas(dtoCampo.getMinFilas());
        campoActual.setMaxFilas(dtoCampo.getMaxFilas());
        campoActual.setMinColumnas(dtoCampo.getMinColumnas());
        campoActual.setMaxColumnas(dtoCampo.getMaxColumnas());
        campoActual.setEstado(dtoCampo.getEstado());
        campoActual.setFechaModificacion(new Date());
        campoActual.setUsuarioModificacion(username);

        if (dtoCampo.getTipoCampo() != null && !dtoCampo.getTipoCampo().isEmpty()) {
            TipoCampo tipoCampo = daoTipoCampo.findByCodigo(dtoCampo.getTipoCampo())
                    .orElseThrow(() -> new DataValidationException("TipoCampo no encontrado: " + dtoCampo.getTipoCampo()));
            campoActual.setTipoCampo(tipoCampo);
        }

        return convertToDto(daoCampo.save(campoActual));
    }

    @Override
    @Transactional
    public void delete(String codigo, String accessToken) {
        validateCodigo(codigo);
        Campo campo = daoCampo.findByCodigo(codigo)
                .orElseThrow(() -> new DataValidationException("No se encontró un campo con el código especificado."));

        List<SeccionCampo> relacionesActivas = daoSeccionCampo.findByCampoCodigo(codigo);
        if (!relacionesActivas.isEmpty()) {
            throw new DataValidationException("No se puede eliminar el campo porque tiene " +
                    relacionesActivas.size() + " relaciones activas con secciones. " +
                    "Primero debe eliminar las relaciones en las secciones asociadas.");
        }

        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        String username = (String) userInfo.get("name");

        campo.setEstado(Estado.I);
        campo.setFechaModificacion(new Date());
        campo.setUsuarioModificacion(username);

        daoCampo.save(campo);
    }

    private String toJsonFromObjects(List<Object> list) {
        try {
            return list != null ? objectMapper.writeValueAsString(list) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private List<Object> fromJsonToObjects(String json) {
        try {
            if (json == null || json.isEmpty()) return null;
            
            // Deserializar directamente como lista de objetos
            return objectMapper.readValue(json, new TypeReference<List<Object>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    private String toJsonFromObject(Object obj) {
        try {
            return obj != null ? objectMapper.writeValueAsString(obj) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Object fromJsonToObject(String json) {
        try {
            if (json == null || json.isEmpty()) return null;
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return null;
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
        if (dtoCampo.getTipoCampo() == null || dtoCampo.getTipoCampo().isEmpty()) {
            throw new DataValidationException("El tipoCampo es requerido");
        }
        
        // Validación específica para tablas
        if ("TABLA".equals(dtoCampo.getTipoCampo())) {
            if (dtoCampo.getTipoConfiguracionTabla() == null) {
                throw new DataValidationException("El tipoConfiguracionTabla es requerido para campos de tipo TABLA");
            }
            
            switch (dtoCampo.getTipoConfiguracionTabla()) {
                case SOLO_COLUMNAS:
                    if (dtoCampo.getColumnas() == null || dtoCampo.getColumnas().isEmpty()) {
                        throw new DataValidationException("Las columnas son requeridas para configuración SOLO_COLUMNAS");
                    }
                    break;
                case SOLO_FILAS:
                    if (dtoCampo.getFilas() == null || dtoCampo.getFilas().isEmpty()) {
                        throw new DataValidationException("Las filas son requeridas para configuración SOLO_FILAS");
                    }
                    break;
                case COLUMNAS_Y_FILAS:
                    if ((dtoCampo.getColumnas() == null || dtoCampo.getColumnas().isEmpty()) ||
                        (dtoCampo.getFilas() == null || dtoCampo.getFilas().isEmpty())) {
                        throw new DataValidationException("Tanto columnas como filas son requeridas para configuración COLUMNAS_Y_FILAS");
                    }
                    break;
            }
        }
    }

    private DtoCampo convertToDto(Campo campo) {
        if (campo == null) return null;
        DtoCampo dto = new DtoCampo();
        dto.setId(campo.getId());
        dto.setCodigo(campo.getCodigo());
        dto.setLabel(campo.getLabel());
        dto.setTipoCampo(campo.getTipoCampo() != null ? campo.getTipoCampo().getCodigo() : null);
        dto.setRequerido(campo.getRequerido());
        dto.setSoloLectura(campo.getSoloLectura());
        dto.setEsMultiple(campo.getEsMultiple());
        
        // Deserializar directamente como objetos
        dto.setOpciones(fromJsonToObjects(campo.getOpciones()));
        dto.setColumnas(fromJsonToObjects(campo.getColumnas()));
        dto.setFilas(fromJsonToObjects(campo.getFilas()));
        dto.setEstructuraTablaPersonalizada(fromJsonToObject(campo.getEstructuraTablaPersonalizada()));
        
        dto.setTipoConfiguracionTabla(campo.getTipoConfiguracionTabla());
        dto.setEsMutable(campo.getEsMutable());
        dto.setMinFilas(campo.getMinFilas());
        dto.setMaxFilas(campo.getMaxFilas());
        dto.setMinColumnas(campo.getMinColumnas());
        dto.setMaxColumnas(campo.getMaxColumnas());
        dto.setEstado(campo.getEstado());
        dto.setUsuarioCreacion(campo.getUsuarioCreacion());
        dto.setFechaCreacion(campo.getFechaCreacion());
        dto.setUsuarioModificacion(campo.getUsuarioModificacion());
        dto.setFechaModificacion(campo.getFechaModificacion());
        return dto;
    }
}