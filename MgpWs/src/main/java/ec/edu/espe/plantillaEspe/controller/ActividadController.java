package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoActividad;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.service.IService.IServiceActividad;
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
 * Controlador REST para la gestión de actividades.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar actividades,
 * así como para obtener listados y paginación de actividades asociadas a proyectos.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/actividad")
public class ActividadController {

    private final IServiceActividad service;

    @Autowired
    public ActividadController(IServiceActividad service) {
        this.service = service;
    }

    /**
     * Obtiene una actividad por su ID.
     *
     * @param id ID de la actividad.
     * @return La actividad encontrada o un error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        if (id == null) {
            return badRequest("El id de la actividad no puede ser nulo.");
        }
        try {
            DtoActividad actividad = service.find(id);
            return ResponseEntity.ok(actividad);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar la actividad.");
        }
    }

    /**
     * Lista todas las actividades activas de un proyecto.
     *
     * @param proyectoId ID del proyecto.
     * @return Lista de actividades activas.
     */
    @GetMapping("/proyecto/{proyectoId}/list")
    public ResponseEntity<?> findAllActivos(@PathVariable Long proyectoId) {
        try {
            List<DtoActividad> actividades = service.findAllActivos(proyectoId);
            return ResponseEntity.ok(actividades);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener las actividades.");
        }
    }

    /**
     * Lista paginada de actividades activas de un proyecto, con filtros opcionales.
     *
     * @param proyectoId     ID del proyecto.
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de actividades activas.
     */
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<?> findAllPaginated(
            @PathVariable Long proyectoId,
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
            // Elimina los parámetros de paginación y ordenamiento del map de filtros
            if (searchCriteria != null) {
                searchCriteria.remove("page");
                searchCriteria.remove("size");
                searchCriteria.remove("sort");
                searchCriteria.remove("direction");
            }
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            Page<DtoActividad> actividades = service.findAllActivos(proyectoId, pageable, searchCriteria);
            return ResponseEntity.ok(actividades);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener las actividades paginadas.");
        }
    }

    /**
     * Lista paginada de todas las actividades activas, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de actividades activas.
     */
    @GetMapping("/list/paginated")
    public ResponseEntity<?> findAllActivosPaginated(
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
            Page<DtoActividad> actividades = service.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(actividades);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener las actividades paginadas.");
        }
    }

    /**
     * Crea una nueva actividad en un proyecto.
     *
     * @param proyectoId ID del proyecto.
     * @param actividad  Datos de la actividad.
     * @param authHeader Cabecera de autorización.
     * @return Actividad creada.
     */
    @PostMapping("/add/proyecto/{proyectoId}")
    public ResponseEntity<?> create(
            @PathVariable Long proyectoId,
            @RequestBody DtoActividad actividad,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoActividad savedActividad = service.save(actividad, proyectoId, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedActividad);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear la actividad.");
        }
    }

    /**
     * Crea una actividad por defecto en un proyecto.
     *
     * @param proyectoId ID del proyecto.
     * @param authHeader Cabecera de autorización.
     * @return Actividad por defecto creada.
     */
    @PostMapping("/default/proyecto/{proyectoId}")
    public ResponseEntity<?> crearActividadDefault(
            @PathVariable Long proyectoId,
            @RequestHeader("Authorization") String authHeader) {
        if (proyectoId == null) {
            return badRequest("El id del proyecto no puede ser nulo.");
        }
        try {
            String token = extractToken(authHeader);
            DtoActividad actividadDefault = service.crearActividadDefault(token, proyectoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(actividadDefault);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear la actividad por defecto.");
        }
    }

    /**
     * Actualiza una actividad existente.
     *
     * @param id         ID de la actividad.
     * @param proyectoId ID del proyecto.
     * @param actividad  Datos de la actividad.
     * @param authHeader Cabecera de autorización.
     * @return Actividad actualizada.
     */
    @PutMapping("/update/{id}/proyecto/{proyectoId}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @PathVariable Long proyectoId,
            @RequestBody DtoActividad actividad,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            actividad.setId(id);
            DtoActividad updatedActividad = service.update(actividad, proyectoId, token);
            return ResponseEntity.ok(updatedActividad);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar la actividad.");
        }
    }

    /**
     * Elimina una actividad por su ID.
     *
     * @param id         ID de la actividad.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta sin contenido si se elimina correctamente.
     */
    @DeleteMapping("/{id}/proyecto")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        if (id == null) {
            return badRequest("El id de la actividad no puede ser nulo.");
        }
        try {
            String token = extractToken(authHeader);
            service.delete(id, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar la actividad."+e);
        }
    }

    private ResponseEntity<String> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + message + "\"}");
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