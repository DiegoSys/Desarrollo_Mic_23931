package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoEstrategia;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.ServiceEstrategia;
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
 * Controlador REST para la gestión de estrategias.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar estrategias,
 * así como para obtener listados y paginación de estrategias.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/estrategia")
public class EstrategiaController {

    private final ServiceEstrategia serviceEstrategia;

    @Autowired
    public EstrategiaController(ServiceEstrategia serviceEstrategia) {
        this.serviceEstrategia = serviceEstrategia;
    }

    /**
     * Obtiene una estrategia por su código.
     *
     * @param codigo Código de la estrategia.
     * @return La estrategia encontrada o un error si no existe.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código de la Estrategia no puede ser nulo o vacío.");
        }

        try {
            DtoEstrategia estrategia = serviceEstrategia.find(codigo);
            return ResponseEntity.ok(estrategia);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar la Estrategia.");
        }
    }

    /**
     * Obtiene una lista de todas las estrategias activas.
     *
     * @return Lista de estrategias activas.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoEstrategia> estrategias = serviceEstrategia.findAllActivos();
            return ResponseEntity.ok(estrategias);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener las estrategias.");
        }
    }

    /**
     * Obtiene una página de estrategias activas, ordenadas y paginadas, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de estrategias activas.
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
            Page<DtoEstrategia> estrategias = serviceEstrategia.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(estrategias);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener las Estrategias paginadas.");
        }
    }

    /**
     * Crea una nueva estrategia.
     *
     * @param estrategia Datos de la estrategia.
     * @param authHeader Cabecera de autorización.
     * @return Estrategia creada.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoEstrategia estrategia,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoEstrategia savedEstrategia = serviceEstrategia.save(estrategia, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEstrategia);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear la Estrategia.");
        }
    }

    /**
     * Actualiza una estrategia existente.
     *
     * @param codigo     Código de la estrategia.
     * @param estrategia Datos de la estrategia.
     * @param authHeader Cabecera de autorización.
     * @return Estrategia actualizada.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoEstrategia estrategia,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            estrategia.setCodigo(codigo);
            DtoEstrategia updatedEstrategia = serviceEstrategia.update(estrategia, token);
            return ResponseEntity.ok(updatedEstrategia);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar la Estrategia.");
        }
    }

    /**
     * Elimina una estrategia por su código.
     *
     * @param codigo     Código de la estrategia.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta sin contenido si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
            @PathVariable String codigo,
            @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código de la Estrategia no puede ser nulo o vacío.");
        }

        try {
            String token = extractToken(authHeader);
            serviceEstrategia.delete(codigo, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar la Estrategia.");
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