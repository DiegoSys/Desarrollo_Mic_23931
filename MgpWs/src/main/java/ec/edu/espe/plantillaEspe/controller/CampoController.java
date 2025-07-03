package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.IService.IServiceCampo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.V1_API_VERSION;

/**
 * Controlador REST para la gestión de campos.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar campos,
 * así como para obtener listados y paginación de campos.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/campo")
public class CampoController {

    private final IServiceCampo service;

    @Autowired
    public CampoController(IServiceCampo service) {
        this.service = service;
    }

    /**
     * Obtiene un campo por su código.
     *
     * @param codigo Código del campo.
     * @return El campo encontrado o un error si no existe.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del campo no puede ser nulo o vacío.");
        }

        try {
            DtoCampo campo = service.find(codigo);
            return ResponseEntity.ok(campo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el campo.");
        }
    }

    /**
     * Obtiene una lista de todos los campos activos.
     *
     * @return Lista de campos activos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoCampo> campos = service.findAllActivos();
            return ResponseEntity.ok(campos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los campos.");
        }
    }

    /**
     * Obtiene una página de campos activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de campos activos.
     */
    @GetMapping
    public ResponseEntity<?> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Map<String, String> searchCriteria) {

        if (page < 0) {
            return badRequest("El número de página no puede ser negativo.");
        }
        if (size <= 0) {
            return badRequest("El tamaño de página debe ser mayor a cero.");
        }
        try {
            if (searchCriteria != null) {
                searchCriteria.remove("page");
                searchCriteria.remove("size");
                searchCriteria.remove("sort");
                searchCriteria.remove("direction");
            }
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            Page<DtoCampo> campos = service.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(campos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los campos paginados." + e);
        }
    }

    /**
     * Crea un nuevo campo.
     *
     * @param campo      Datos del campo.
     * @param authHeader Cabecera de autorización.
     * @return Campo creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoCampo campo,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoCampo savedCampo = service.save(campo, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCampo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el campo." + e);
        }
    }

    /**
     * Actualiza un campo existente.
     *
     * @param codigo     Código del campo.
     * @param campo      Datos del campo.
     * @param authHeader Cabecera de autorización.
     * @return Campo actualizado.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoCampo campo,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            campo.setCodigo(codigo);
            DtoCampo updatedCampo = service.update(campo, token);
            return ResponseEntity.ok(updatedCampo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el campo.");
        }
    }

    /**
     * Elimina un campo por su código.
     *
     * @param codigo     Código del campo.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta sin contenido si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
            @PathVariable String codigo,
            @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del campo no puede ser nulo o vacío.");
        }

        try {
            String token = extractToken(authHeader);
            service.delete(codigo, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el campo."+ e);
        }
    }

    private ResponseEntity<String> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + message + "\"}");
    }

    private ResponseEntity<String> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + message + "\"}");
    }

    private ResponseEntity<String> internalServerError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"" + message + "\"}");
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new DataValidationException("Token de autorización no válido");
    }
}