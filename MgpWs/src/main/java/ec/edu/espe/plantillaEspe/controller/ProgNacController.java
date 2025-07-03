package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoProgNac;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.ServiceProgNac;
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
 * Controlador REST para la gestión de programas nacionales.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar programas nacionales,
 * así como para obtener listados y paginación.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/prognacional")
public class ProgNacController {

    private final ServiceProgNac serviceProgNac;

    @Autowired
    public ProgNacController(ServiceProgNac serviceProgNac) {
        this.serviceProgNac = serviceProgNac;
    }

    /**
     * Obtiene un programa nacional por su código.
     *
     * @param codigo Código del programa nacional.
     * @return El programa encontrado o un error si no existe.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del Programa Nacional no puede ser nulo o vacío.");
        }

        try {
            DtoProgNac progNac = serviceProgNac.find(codigo);
            return ResponseEntity.ok(progNac);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el Programa Nacional.");
        }
    }

    /**
     * Obtiene una lista de todos los programas nacionales activos.
     *
     * @return Lista de programas nacionales activos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoProgNac> progNacs = serviceProgNac.findAllActivos();
            return ResponseEntity.ok(progNacs);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los programas nacionales.");
        }
    }

    /**
     * Obtiene una página de programas nacionales activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de programas nacionales activos.
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
            Page<DtoProgNac> progNacs = serviceProgNac.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(progNacs);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los Programas Nacionales paginados.");
        }
    }

    /**
     * Crea un nuevo programa nacional.
     *
     * @param progNac    Datos del programa nacional.
     * @param authHeader Cabecera de autorización.
     * @return Programa nacional creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoProgNac progNac,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoProgNac savedProgNac = serviceProgNac.save(progNac, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProgNac);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el Programa Nacional.");
        }
    }

    /**
     * Actualiza un programa nacional existente.
     *
     * @param codigo     Código del programa nacional.
     * @param progNac    Datos del programa nacional.
     * @param authHeader Cabecera de autorización.
     * @return Programa nacional actualizado.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoProgNac progNac,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            progNac.setCodigo(codigo);
            DtoProgNac updatedProgNac = serviceProgNac.update(progNac, token);
            return ResponseEntity.ok(updatedProgNac);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el Programa Nacional.");
        }
    }

    /**
     * Elimina un programa nacional por su código.
     *
     * @param codigo     Código del programa nacional.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta sin contenido si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
            @PathVariable String codigo,
            @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del Programa Nacional no puede ser nulo o vacío.");
        }

        try {
            String token = extractToken(authHeader);
            serviceProgNac.delete(codigo, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el Programa Nacional.");
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