package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoObjEstrategico;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.ServiceObjEstrategico;
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
 * Controlador REST para la gestión de objetivos estratégicos.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar objetivos estratégicos,
 * así como para obtener listados y paginación de objetivos.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/objestrategico")
public class ObjEstrategicoController {

    private final ServiceObjEstrategico serviceObjEstrategico;

    @Autowired
    public ObjEstrategicoController(ServiceObjEstrategico serviceObjEstrategico) {
        this.serviceObjEstrategico = serviceObjEstrategico;
    }

    /**
     * Obtiene un objetivo estratégico por su código.
     *
     * @param codigo Código del objetivo estratégico.
     * @return El objetivo encontrado o un error si no existe.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del Objetivo Estratégico no puede ser nulo o vacío.");
        }

        try {
            DtoObjEstrategico objEstrategico = serviceObjEstrategico.find(codigo);
            return ResponseEntity.ok(objEstrategico);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el Objetivo Estratégico.");
        }
    }

    /**
     * Obtiene una lista de todos los objetivos estratégicos activos.
     *
     * @return Lista de objetivos estratégicos activos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoObjEstrategico> objetivos = serviceObjEstrategico.findAllActivos();
            return ResponseEntity.ok(objetivos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los objetivos estratégicos.");
        }
    }

    /**
     * Obtiene una página de objetivos estratégicos activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de objetivos estratégicos activos.
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
            Page<DtoObjEstrategico> objEstrategicos = serviceObjEstrategico.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(objEstrategicos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los Objetivos Estratégicos paginados.");
        }
    }

    /**
     * Crea un nuevo objetivo estratégico.
     *
     * @param objEstrategico Datos del objetivo estratégico.
     * @param authHeader     Cabecera de autorización.
     * @return Objetivo estratégico creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoObjEstrategico objEstrategico,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoObjEstrategico savedObjEstrategico = serviceObjEstrategico.save(objEstrategico, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedObjEstrategico);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el Objetivo Estratégico.");
        }
    }

    /**
     * Actualiza un objetivo estratégico existente.
     *
     * @param codigo         Código del objetivo estratégico.
     * @param objEstrategico Datos del objetivo estratégico.
     * @param authHeader     Cabecera de autorización.
     * @return Objetivo estratégico actualizado.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoObjEstrategico objEstrategico,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            objEstrategico.setCodigo(codigo);
            DtoObjEstrategico updatedObjEstrategico = serviceObjEstrategico.update(objEstrategico, token);
            return ResponseEntity.ok(updatedObjEstrategico);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el Objetivo Estratégico.");
        }
    }

    /**
     * Elimina un objetivo estratégico por su código.
     *
     * @param codigo     Código del objetivo estratégico.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta sin contenido si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
            @PathVariable String codigo,
            @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del Objetivo Estratégico no puede ser nulo o vacío.");
        }

        try {
            String token = extractToken(authHeader);
            serviceObjEstrategico.delete(codigo, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el Objetivo Estratégico.");
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