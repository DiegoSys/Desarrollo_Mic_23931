package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoSubPrograma;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.service.IService.IServiceSubPrograma;
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
 * Controlador REST para la gestión de subprogramas.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar subprogramas,
 * así como para obtener listados y paginación de subprogramas.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/subprograma")
public class SubProgramaController {

    private final IServiceSubPrograma service;

    @Autowired
    public SubProgramaController(IServiceSubPrograma service) {
        this.service = service;
    }

    /**
     * Obtiene un subprograma por su id.
     *
     * @param id Id del subprograma.
     * @return Subprograma encontrado o error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        if (id == null) {
            return badRequest("El id del subprograma no puede ser nulo.");
        }
        try {
            DtoSubPrograma subPrograma = service.find(id);
            return ResponseEntity.ok(subPrograma);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el subprograma.");
        }
    }

    /**
     * Obtiene un subprograma por su código y el id del programa.
     *
     * @param codigo Código del subprograma.
     * @param programaId Id del programa.
     * @return Subprograma encontrado o error si no existe.
     */
    @GetMapping("/codigo/{codigo}/programa/{programaId}")
    public ResponseEntity<?> findByCodigoAndProgramaId(
            @PathVariable String codigo,
            @PathVariable Long programaId) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del subprograma no puede ser nulo o vacío.");
        }
        if (programaId == null) {
            return badRequest("El id del programa no puede ser nulo.");
        }
        try {
            DtoSubPrograma subPrograma = service.findByCodigoAndProgramaId(codigo, programaId);
            return ResponseEntity.ok(subPrograma);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el subprograma.");
        }
    }

    /**
     * Obtiene una lista de todos los subprogramas.
     *
     * @return Lista de subprogramas.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoSubPrograma> subProgramas = service.findAll();
            return ResponseEntity.ok(subProgramas);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los subprogramas.");
        }
    }

    /**
     * Obtiene una página de subprogramas, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de subprogramas.
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
            Page<DtoSubPrograma> subProgramas = service.findAll(pageable, searchCriteria);
            return ResponseEntity.ok(subProgramas);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los subprogramas paginados.");
        }
    }

    /**
     * Obtiene una lista de todos los subprogramas activos.
     *
     * @return Lista de subprogramas activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<?> findAllActivos() {
        try {
            List<DtoSubPrograma> subProgramas = service.findAllActivos();
            return ResponseEntity.ok(subProgramas);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los subprogramas activos.");
        }
    }

    /**
     * Obtiene una página de subprogramas activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de subprogramas activos.
     */
    @GetMapping("/activos/paginated")
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
            Page<DtoSubPrograma> subProgramas = service.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(subProgramas);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los subprogramas activos paginados.");
        }
    }

    /**
     * Obtiene una página de subprogramas filtrados por programa y estado.
     *
     * @param programaId     Id del programa.
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de subprogramas filtrados por programa y estado.
     */
    @GetMapping("/programa/{programaId}/estado")
    public ResponseEntity<?> findByProgramaIdAndEstado(
            @PathVariable Long programaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Map<String, String> searchCriteria) {

        if (programaId == null) {
            return badRequest("El id del programa no puede ser nulo.");
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
            Page<DtoSubPrograma> subProgramas = service.findByProgramaIdAndEstado(programaId, pageable, searchCriteria);
            return ResponseEntity.ok(subProgramas);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los subprogramas por programa y estado.");
        }
    }

    /**
     * Crea un nuevo subprograma.
     *
     * @param subPrograma Datos del subprograma.
     * @param authHeader  Cabecera de autorización.
     * @return Subprograma creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoSubPrograma subPrograma,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoSubPrograma savedSubPrograma = service.save(subPrograma, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSubPrograma);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el subprograma.");
        }
    }

    /**
     * Crea un subprograma por defecto para un programa.
     *
     * @param programaId Id del programa.
     * @param authHeader Cabecera de autorización.
     * @return Subprograma por defecto creado.
     */
    @PostMapping("/default/{programaId}")
    public ResponseEntity<?> crearSubProgramaDefault(
            @PathVariable Long programaId,
            @RequestHeader("Authorization") String authHeader) {
        if (programaId == null) {
            return badRequest("El id del programa no puede ser nulo.");
        }
        try {
            String token = extractToken(authHeader);
            DtoSubPrograma subProgramaDefault = service.crearSubProgramaDefault(token, programaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(subProgramaDefault);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el subprograma por defecto.");
        }
    }

    /**
     * Actualiza un subprograma existente.
     *
     * @param id         Id del subprograma.
     * @param subPrograma Datos del subprograma.
     * @param authHeader Cabecera de autorización.
     * @return Subprograma actualizado.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DtoSubPrograma subPrograma,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            subPrograma.setId(id);
            DtoSubPrograma updatedSubPrograma = service.update(subPrograma, id, token);
            return ResponseEntity.ok(updatedSubPrograma);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el subprograma.");
        }
    }

    /**
     * Elimina un subprograma por su id.
     *
     * @param id         Id del subprograma.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta vacía si se elimina correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        if (id == null) {
            return badRequest("El id del subprograma no puede ser nulo.");
        }
        try {
            String token = extractToken(authHeader);
            service.delete(id, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el subprograma.");
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