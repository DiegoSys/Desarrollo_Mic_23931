package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoPrograma;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.IService.IServicePrograma;
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
 * Controlador REST para la gestión de programas.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar programas,
 * así como para obtener listados y paginación de programas.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/programa")
public class ProgramaController {

    private final IServicePrograma service;

    @Autowired
    public ProgramaController(IServicePrograma service) {
        this.service = service;
    }

    /**
     * Obtiene un programa por su id.
     *
     * @param id Id del programa.
     * @return El programa encontrado o error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> find(@PathVariable Long id) {
        if (id == null) {
            return badRequest("El id del programa no puede ser nulo o vacío.");
        }
        try {
            DtoPrograma programa = service.find(id);
            return ResponseEntity.ok(programa);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el programa.");
        }
    }

    /**
     * Obtiene una lista de todos los programas activos.
     *
     * @return Lista de programas activos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoPrograma> programas = service.findAllActivos();
            return ResponseEntity.ok(programas);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los programas.");
        }
    }

    /**
     * Obtiene una página de programas activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de programas activos.
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
            Page<DtoPrograma> programas = service.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(programas);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los programas paginados.");
        }
    }

    /**
     * Crea un nuevo programa.
     *
     * @param programa   Datos del programa.
     * @param authHeader Cabecera de autorización.
     * @return Programa creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoPrograma programa,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoPrograma savedPrograma = service.save(programa, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrograma);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el programa.");
        }
    }

    /**
     * Actualiza un programa existente.
     *
     * @param id         Id del programa.
     * @param programa   Datos del programa.
     * @param authHeader Cabecera de autorización.
     * @return Programa actualizado.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DtoPrograma programa,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            programa.setId(id);
            DtoPrograma updatedPrograma = service.update(programa, token);
            return ResponseEntity.ok(updatedPrograma);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el programa.");
        }
    }

    /**
     * Elimina un programa por su id.
     *
     * @param id         Id del programa.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta vacía si se elimina correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        if (id == null) {
            return badRequest("El id del programa no puede ser nulo o vacío.");
        }
        try {
            String token = extractToken(authHeader);
            service.delete(id, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el programa.");
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