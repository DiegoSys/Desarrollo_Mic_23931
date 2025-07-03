package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoObjOperativo;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.IService.IServiceObjOperativo;
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
 * Controlador REST para la gestión de objetivos operativos.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar objetivos operativos,
 * así como para obtener listados y paginación de objetivos.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/objoperativos")
public class ObjOperativoController {

    private final IServiceObjOperativo service;

    @Autowired
    public ObjOperativoController(IServiceObjOperativo service) {
        this.service = service;
    }

    /**
     * Obtiene un objetivo operativo por su código.
     *
     * @param codigo Código del objetivo operativo.
     * @return El objetivo encontrado o un error si no existe.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del Objetivo Operativo no puede ser nulo o vacío.");
        }

        try {
            DtoObjOperativo objOperativo = service.find(codigo);
            return ResponseEntity.ok(objOperativo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el Objetivo Operativo.");
        }
    }

    /**
     * Obtiene una lista de todos los objetivos operativos activos.
     *
     * @return Lista de objetivos operativos activos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoObjOperativo> objetivos = service.findAllActivos();
            return ResponseEntity.ok(objetivos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los objetivos operativos.");
        }
    }

    /**
     * Obtiene una página de objetivos operativos activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de objetivos operativos activos.
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
            Page<DtoObjOperativo> objetivos = service.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(objetivos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los Objetivos Operativos paginados.");
        }
    }

    /**
     * Crea un nuevo objetivo operativo.
     *
     * @param dto        Datos del objetivo operativo.
     * @param authHeader Cabecera de autorización.
     * @return Objetivo operativo creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoObjOperativo dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoObjOperativo savedObj = service.save(dto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedObj);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el Objetivo Operativo.");
        }
    }

    /**
     * Actualiza un objetivo operativo existente.
     *
     * @param codigo     Código del objetivo operativo.
     * @param dto        Datos del objetivo operativo.
     * @param authHeader Cabecera de autorización.
     * @return Objetivo operativo actualizado.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoObjOperativo dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            dto.setCodigo(codigo);
            DtoObjOperativo updatedObj = service.update(dto, token);
            return ResponseEntity.ok(updatedObj);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el Objetivo Operativo.");
        }
    }

    /**
     * Elimina un objetivo operativo por su código.
     *
     * @param codigo     Código del objetivo operativo.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta sin contenido si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
            @PathVariable String codigo,
            @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del Objetivo Operativo no puede ser nulo o vacío.");
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
            return internalServerError("Ocurrió un error interno al eliminar el Objetivo Operativo.");
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