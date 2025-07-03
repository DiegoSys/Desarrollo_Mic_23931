package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoTipoProyecto;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.IService.IServiceTipoProyecto;
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
 * Controlador REST para la gestión de tipos de proyecto.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar tipos de proyecto,
 * así como para obtener listados y paginación.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/tipoproyecto")
public class TipoProyectoController {

    private final IServiceTipoProyecto service;

    @Autowired
    public TipoProyectoController(IServiceTipoProyecto service) {
        this.service = service;
    }

    /**
     * Obtiene un tipo de proyecto por su código.
     *
     * @param codigo Código del tipo de proyecto.
     * @return Tipo de proyecto encontrado o error si no existe.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del proyecto no puede ser nulo o vacío.");
        }

        try {
            DtoTipoProyecto proyecto = service.find(codigo);
            return ResponseEntity.ok(proyecto);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el proyecto.");
        }
    }

    /**
     * Obtiene una lista de todos los tipos de proyecto activos.
     *
     * @return Lista de tipos de proyecto activos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoTipoProyecto> proyectos = service.findAllActivos();
            return ResponseEntity.ok(proyectos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los proyectos.");
        }
    }

    /**
     * Obtiene una página de tipos de proyecto activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de tipos de proyecto activos.
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
            Page<DtoTipoProyecto> proyectos = service.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(proyectos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los proyectos paginados.");
        }
    }

    /**
     * Crea un nuevo tipo de proyecto.
     *
     * @param proyecto   Datos del tipo de proyecto.
     * @param authHeader Cabecera de autorización.
     * @return Tipo de proyecto creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoTipoProyecto proyecto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoTipoProyecto savedProyecto = service.save(proyecto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProyecto);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el proyecto.");
        }
    }

    /**
     * Actualiza un tipo de proyecto existente.
     *
     * @param codigo     Código del tipo de proyecto.
     * @param proyecto   Datos del tipo de proyecto.
     * @param authHeader Cabecera de autorización.
     * @return Tipo de proyecto actualizado.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoTipoProyecto proyecto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            proyecto.setCodigo(codigo);
            DtoTipoProyecto updatedProyecto = service.update(proyecto, token);
            return ResponseEntity.ok(updatedProyecto);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el proyecto.");
        }
    }

    /**
     * Elimina un tipo de proyecto por su código.
     *
     * @param codigo     Código del tipo de proyecto.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta vacía si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
        @PathVariable String codigo,
        @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del proyecto no puede ser nulo o vacío.");
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
            return internalServerError("Ocurrió un error interno al eliminar el proyecto.");
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