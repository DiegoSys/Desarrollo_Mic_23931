package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoSeccion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.IService.IServiceSeccion;
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
 * Controlador REST para la gestión de secciones.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar secciones,
 * así como para obtener listados y paginación de secciones.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/seccion")
public class SeccionController {

    private final IServiceSeccion service;

    @Autowired
    public SeccionController(IServiceSeccion service) {
        this.service = service;
    }

    /**
     * Obtiene una sección por su código.
     *
     * @param codigo Código de la sección.
     * @return Sección encontrada o error si no se encuentra.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código de la sección no puede ser nulo o vacío.");
        }

        try {
            DtoSeccion seccion = service.find(codigo);
            return ResponseEntity.ok(seccion);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar la sección.");
        }
    }

    /**
     * Obtiene una lista de todas las secciones activas.
     *
     * @return Lista de secciones activas.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoSeccion> secciones = service.findAllActivos();
            return ResponseEntity.ok(secciones);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener las secciones.");
        }
    }

    /**
     * Obtiene una página de secciones activas, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de secciones activas.
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
            Page<DtoSeccion> secciones = service.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(secciones);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener las secciones paginadas." + e);
        }
    }

    /**
     * Crea una nueva sección.
     *
     * @param seccion    Datos de la sección.
     * @param authHeader Cabecera de autorización.
     * @return Sección creada.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoSeccion seccion,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoSeccion savedSeccion = service.save(seccion, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSeccion);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear la sección." + e);
        }
    }

    /**
     * Actualiza una sección existente.
     *
     * @param codigo     Código de la sección.
     * @param seccion    Datos de la sección.
     * @param authHeader Cabecera de autorización.
     * @return Sección actualizada.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoSeccion seccion,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            seccion.setCodigo(codigo);
            DtoSeccion updatedSeccion = service.update(seccion, token);
            return ResponseEntity.ok(updatedSeccion);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar la sección.");
        }
    }

    /**
     * Elimina una sección por su código.
     *
     * @param codigo     Código de la sección.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta vacía si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
            @PathVariable String codigo,
            @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código de la sección no puede ser nulo o vacío.");
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
            return internalServerError("Ocurrió un error interno al eliminar la sección.");
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