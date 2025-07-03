package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoPresSubgrupo;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.IService.IServicePresSubgrupo;
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
 * Controlador REST para la gestión de subgrupos presupuestarios.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar subgrupos,
 * así como para obtener listados y paginación de subgrupos.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/pressubgrupo")
public class PresSubgrupoController {

    private final IServicePresSubgrupo service;

    @Autowired
    public PresSubgrupoController(IServicePresSubgrupo service) {
        this.service = service;
    }

    /**
     * Obtiene un subgrupo por su código.
     *
     * @param codigo Código del subgrupo.
     * @return Subgrupo encontrado o error si no se encuentra.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del subgrupo no puede ser nulo o vacío.");
        }

        try {
            DtoPresSubgrupo presSubgrupo = service.find(codigo);
            return ResponseEntity.ok(presSubgrupo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el subgrupo.");
        }
    }

    /**
     * Obtiene una lista de todos los subgrupos.
     *
     * @return Lista de subgrupos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoPresSubgrupo> presSubgrupos = service.findAll();
            return ResponseEntity.ok(presSubgrupos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los subgrupos.");
        }
    }

    /**
     * Obtiene una página de subgrupos activos, con paginación.
     *
     * @param page      Número de página.
     * @param size      Tamaño de página.
     * @param sort      Campo de ordenamiento.
     * @param direction Dirección de ordenamiento (asc/desc).
     * @return Página de subgrupos activos.
     */
    @GetMapping
    public ResponseEntity<?> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        if (page < 0) {
            return badRequest("El número de página no puede ser negativo.");
        }

        if (size <= 0) {
            return badRequest("El tamaño de página debe ser mayor a cero.");
        }

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            Page<DtoPresSubgrupo> presSubgrupos = service.findAllActivos(pageable);
            return ResponseEntity.ok(presSubgrupos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los subgrupos paginados.");
        }
    }

    /**
     * Obtiene una página de subgrupos filtrados por el código de grupo.
     *
     * @param codigo        Código del grupo.
     * @param page          Número de página.
     * @param size          Tamaño de página.
     * @param sort          Campo de ordenamiento.
     * @param direction     Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de subgrupos filtrados por grupo.
     */
    @GetMapping("/grupo/{codigo}")
    public ResponseEntity<?> findByPresGrupoCodigo(
            @PathVariable String codigo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Map<String, String> searchCriteria) {

        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del grupo no puede ser nulo o vacío.");
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
            Page<DtoPresSubgrupo> presSubgrupos = service.findByPresGrupo_Codigo(codigo, pageable, searchCriteria);
            return ResponseEntity.ok(presSubgrupos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los subgrupos por grupo.");
        }
    }

    /**
     * Crea un nuevo subgrupo.
     *
     * @param presSubgrupo Datos del subgrupo.
     * @param authHeader   Cabecera de autorización.
     * @return Subgrupo creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoPresSubgrupo presSubgrupo,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoPresSubgrupo savedPresSubgrupo = service.save(presSubgrupo, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPresSubgrupo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el subgrupo.");
        }
    }

    /**
     * Actualiza un subgrupo existente.
     *
     * @param codigo       Código del subgrupo.
     * @param presSubgrupo Datos del subgrupo.
     * @param authHeader   Cabecera de autorización.
     * @return Subgrupo actualizado.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoPresSubgrupo presSubgrupo,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            presSubgrupo.setCodigo(codigo);
            DtoPresSubgrupo updatedPresSubgrupo = service.update(presSubgrupo, token);
            return ResponseEntity.ok(updatedPresSubgrupo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el subgrupo.");
        }
    }

    /**
     * Elimina un subgrupo por su código.
     *
     * @param codigo     Código del subgrupo.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta vacía si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
        @PathVariable String codigo,
        @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del subgrupo no puede ser nulo o vacío.");
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
            return internalServerError("Ocurrió un error interno al eliminar el subgrupo.");
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