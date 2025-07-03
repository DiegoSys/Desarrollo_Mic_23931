package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoPresGrupo;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.IService.IServicePresGrupo;
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
 * Controlador REST para la gestión de grupos presupuestarios.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar grupos,
 * así como para obtener listados y paginación de grupos.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/presgrupo")
public class PresGrupoController {

    private final IServicePresGrupo service;

    @Autowired
    public PresGrupoController(IServicePresGrupo service) {
        this.service = service;
    }

    /**
     * Obtiene un grupo por su código.
     *
     * @param codigo Código del grupo.
     * @return Grupo encontrado o error si no se encuentra.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del grupo no puede ser nulo o vacío.");
        }

        try {
            DtoPresGrupo presGrupo = service.find(codigo);
            return ResponseEntity.ok(presGrupo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el grupo.");
        }
    }

    /**
     * Obtiene una lista de todos los grupos activos.
     *
     * @return Lista de grupos activos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoPresGrupo> presGrupos = service.findAllActivos();
            return ResponseEntity.ok(presGrupos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los grupos.");
        }
    }

    /**
     * Obtiene una página de grupos activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de grupos activos.
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
            Page<DtoPresGrupo> presGrupos = service.findAllActivos(pageable, searchCriteria );
            return ResponseEntity.ok(presGrupos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los grupos paginados.");
        }
    }

    /**
     * Crea un nuevo grupo.
     *
     * @param presGrupo  Datos del grupo.
     * @param authHeader Cabecera de autorización.
     * @return Grupo creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoPresGrupo presGrupo,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoPresGrupo savedPresGrupo = service.save(presGrupo, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPresGrupo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el grupo.");
        }
    }

    /**
     * Actualiza un grupo existente.
     *
     * @param codigo     Código del grupo.
     * @param presGrupo  Datos del grupo.
     * @param authHeader Cabecera de autorización.
     * @return Grupo actualizado.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoPresGrupo presGrupo,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            presGrupo.setCodigo(codigo);
            DtoPresGrupo updatedPresGrupo = service.update(presGrupo, token);
            return ResponseEntity.ok(updatedPresGrupo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el grupo.");
        }
    }

    /**
     * Obtiene una página de grupos filtrados por el código de naturaleza.
     *
     * @param codigo        Código de la naturaleza.
     * @param page          Número de página.
     * @param size          Tamaño de página.
     * @param sort          Campo de ordenamiento.
     * @param direction     Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de grupos filtrados por naturaleza.
     */
    @GetMapping("/naturaleza/{codigo}")
    public ResponseEntity<?> findByPresNaturaleza_Codigo(
            @PathVariable String codigo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Map<String, String> searchCriteria) {

        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código de naturaleza no puede ser nulo o vacío.");
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
            Page<DtoPresGrupo> presGrupos = service.findByPresNaturaleza_Codigo(codigo, pageable, searchCriteria);
            return ResponseEntity.ok(presGrupos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los grupos por naturaleza.");
        }
    }

    /**
     * Elimina un grupo por su código.
     *
     * @param codigo     Código del grupo.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta vacía si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
        @PathVariable String codigo,
        @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del grupo no puede ser nulo o vacío.");
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
            return internalServerError("Ocurrió un error interno al eliminar el grupo.");
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