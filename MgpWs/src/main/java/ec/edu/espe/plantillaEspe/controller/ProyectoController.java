package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProyecto;
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
 * Controlador REST para la gestión de proyectos.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar proyectos,
 * así como para obtener listados y paginación de proyectos.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/proyecto")
public class ProyectoController {

    private final IServiceProyecto service;

    @Autowired
    public ProyectoController(IServiceProyecto service) {
        this.service = service;
    }

    /**
     * Obtiene un proyecto por su id.
     *
     * @param id Id del proyecto.
     * @return El proyecto encontrado o error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        if (id == null) {
            return badRequest("El id del proyecto no puede ser nulo.");
        }
        try {
            DtoProyecto proyecto = service.find(id);
            return ResponseEntity.ok(proyecto);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el proyecto.");
        }
    }

    /**
     * Obtiene una lista de proyectos activos por programa y subprograma.
     *
     * @param programaId   Id del programa.
     * @param subProgramaId Id del subprograma.
     * @return Lista de proyectos activos.
     */
    @GetMapping("/programa/{programaId}/subprograma/{subProgramaId}/list")
    public ResponseEntity<?> findAllActivos(
            @PathVariable Long programaId,
            @PathVariable Long subProgramaId) {
        try {
            List<DtoProyecto> proyectos = service.findAllActivos(programaId, subProgramaId);
            return ResponseEntity.ok(proyectos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los proyectos.");
        }
    }

    /**
     * Obtiene una página de proyectos por programa y subprograma, con filtros opcionales.
     *
     * @param programaId    Id del programa.
     * @param subProgramaId Id del subprograma.
     * @param page          Número de página.
     * @param size          Tamaño de página.
     * @param sort          Campo de ordenamiento.
     * @param direction     Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de proyectos.
     */
    @GetMapping("/programa/{programaId}/subprograma/{subProgramaId}")
    public ResponseEntity<?> findAllPaginated(
            @PathVariable Long programaId,
            @PathVariable Long subProgramaId,
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
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            Page<DtoProyecto> proyectos = service.findAll(programaId, subProgramaId, pageable, searchCriteria);
            return ResponseEntity.ok(proyectos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los proyectos paginados.");
        }
    }

    /**
     * Obtiene una página de proyectos activos por programa y subprograma, con filtros opcionales.
     *
     * @param programaId    Id del programa.
     * @param subProgramaId Id del subprograma.
     * @param page          Número de página.
     * @param size          Tamaño de página.
     * @param sort          Campo de ordenamiento.
     * @param direction     Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de proyectos activos.
     */
    @GetMapping("/programa/{programaId}/subprograma/{subProgramaId}/activos")
    public ResponseEntity<?> findByProgramaAndSubprograma(
            @PathVariable Long programaId,
            @PathVariable Long subProgramaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Map<String, String> searchCriteria) {

        if (programaId == null) {
            return badRequest("El id del programa no puede ser nulo.");
        }
        if (subProgramaId == null) {
            return badRequest("El id del subprograma no puede ser nulo.");
        }
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
            Page<DtoProyecto> proyectos = service.findByProgramaAndSubprograma(programaId, subProgramaId, pageable, searchCriteria);
            return ResponseEntity.ok(proyectos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los proyectos por programa y subprograma.");
        }
    }

    /**
     * Crea un nuevo proyecto.
     *
     * @param programaId    Id del programa.
     * @param subProgramaId Id del subprograma.
     * @param proyecto      Datos del proyecto.
     * @param authHeader    Cabecera de autorización.
     * @return Proyecto creado.
     */
    @PostMapping("/add/programa/{programaId}/subprograma/{subProgramaId}")
    public ResponseEntity<?> create(
            @PathVariable Long programaId,
            @PathVariable Long subProgramaId,
            @RequestBody DtoProyecto proyecto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoProyecto savedProyecto = service.save(proyecto, programaId, subProgramaId, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProyecto);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el proyecto.");
        }
    }

    /**
     * Crea un proyecto por defecto para un programa y subprograma.
     *
     * @param programaId    Id del programa.
     * @param subProgramaId Id del subprograma.
     * @param authHeader    Cabecera de autorización.
     * @return Proyecto por defecto creado.
     */
    @PostMapping("/default/programa/{programaId}/subprograma/{subProgramaId}")
    public ResponseEntity<?> crearProyectoDefault(
            @PathVariable Long programaId,
            @PathVariable Long subProgramaId,
            @RequestHeader("Authorization") String authHeader) {
        if (subProgramaId == null) {
            return badRequest("El id del subprograma no puede ser nulo.");
        }
        try {
            String token = extractToken(authHeader);
            DtoProyecto proyectoDefault = service.crearProyectoDefault(token, programaId, subProgramaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(proyectoDefault);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el proyecto por defecto.");
        }
    }

    /**
     * Actualiza un proyecto existente.
     *
     * @param id         Id del proyecto.
     * @param proyecto   Datos del proyecto.
     * @param authHeader Cabecera de autorización.
     * @return Proyecto actualizado.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DtoProyecto proyecto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            proyecto.setId(id);
            DtoProyecto updatedProyecto = service.update(proyecto, id, token);
            return ResponseEntity.ok(updatedProyecto);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el proyecto.");
        }
    }

    /**
     * Elimina un proyecto por su id, programa y subprograma.
     *
     * @param id           Id del proyecto.
     * @param programaId   Id del programa.
     * @param subProgramaId Id del subprograma.
     * @param authHeader   Cabecera de autorización.
     * @return Respuesta vacía si se elimina correctamente.
     */
    @DeleteMapping("/{id}/programa/{programaId}/subprograma/{subProgramaId}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @PathVariable Long programaId,
            @PathVariable Long subProgramaId,
            @RequestHeader("Authorization") String authHeader) {
        if (id == null) {
            return badRequest("El id del proyecto no puede ser nulo.");
        }
        if (subProgramaId == null) {
            return badRequest("El id del subprograma no puede ser nulo.");
        }
        if (programaId == null) {
            return badRequest("El id del programa no puede ser nulo.");
        }
        try {
            String token = extractToken(authHeader);
            service.delete(id, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el proyecto.");
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