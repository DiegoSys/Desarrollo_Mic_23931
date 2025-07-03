package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoOPN;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.ServiceOPN;
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
 * Controlador REST para la gestión de Objetivos del Plan Nacional (OPN).
 * Proporciona endpoints para consultar, crear, actualizar y eliminar OPN,
 * así como para obtener listados y paginación de OPN.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/opn")
public class OPNController {

    private final ServiceOPN serviceOPN;

    @Autowired
    public OPNController(ServiceOPN serviceOPN) {
        this.serviceOPN = serviceOPN;
    }

    /**
     * Obtiene un OPN por su código.
     *
     * @param codigo Código del OPN.
     * @return El OPN encontrado o un error si no existe.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del OPN no puede ser nulo o vacío.");
        }

        try {
            DtoOPN opn = serviceOPN.find(codigo);
            return ResponseEntity.ok(opn);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el OPN.");
        }
    }

    /**
     * Obtiene una lista de todos los OPN activos.
     *
     * @return Lista de OPN activos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoOPN> opns = serviceOPN.findAllActivos();
            return ResponseEntity.ok(opns);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los OPNs.");
        }
    }

    /**
     * Obtiene una página de OPN activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de OPN activos.
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
            Page<DtoOPN> opns = serviceOPN.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(opns);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los OPNs paginados.");
        }
    }

    /**
     * Crea un nuevo OPN.
     *
     * @param opn        Datos del OPN.
     * @param authHeader Cabecera de autorización.
     * @return OPN creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoOPN opn,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoOPN savedOPN = serviceOPN.save(opn, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOPN);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el OPN.");
        }
    }

    /**
     * Actualiza un OPN existente.
     *
     * @param codigo     Código del OPN.
     * @param opn        Datos del OPN.
     * @param authHeader Cabecera de autorización.
     * @return OPN actualizado.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoOPN opn,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            opn.setCodigo(codigo);
            DtoOPN updatedOPN = serviceOPN.update(opn, token);
            return ResponseEntity.ok(updatedOPN);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el OPN.");
        }
    }

    /**
     * Elimina un OPN por su código.
     *
     * @param codigo     Código del OPN.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta sin contenido si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
            @PathVariable String codigo,
            @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del OPN no puede ser nulo o vacío.");
        }

        try {
            String token = extractToken(authHeader);

            serviceOPN.delete(codigo, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el OPN.");
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