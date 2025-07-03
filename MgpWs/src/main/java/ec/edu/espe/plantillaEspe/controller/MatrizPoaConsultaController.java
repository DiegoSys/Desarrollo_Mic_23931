package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoMatrizPoaConsulta;
import ec.edu.espe.plantillaEspe.dto.DtoPOA;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.service.IService.IServiceMatrizPoaConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.V1_API_VERSION;

/**
 * Controlador REST para la gestión y consulta de la matriz POA.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar POA,
 * así como para obtener listados y paginación de la matriz POA.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/poa")
public class MatrizPoaConsultaController {

    private final IServiceMatrizPoaConsulta service;

    @Autowired
    public MatrizPoaConsultaController(IServiceMatrizPoaConsulta service) {
        this.service = service;
    }

    /**
     * Obtiene una página de la matriz POA, con filtros y paginación.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de la matriz POA.
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
            Page<DtoMatrizPoaConsulta> matriz = service.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(matriz);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener la matriz POA.");
        }
    }

    /**
     * Obtiene un POA por su ID.
     *
     * @param id ID del POA.
     * @return El POA encontrado o un error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        if (id == null) {
            return badRequest("El id del POA no puede ser nulo.");
        }
        try {
            DtoPOA dto = service.find(id);
            return ResponseEntity.ok(dto);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el POA.");
        }
    }

    /**
     * Crea un nuevo POA.
     *
     * @param dto       Datos del POA.
     * @param authHeader Cabecera de autorización.
     * @return POA creado.
     */
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody DtoPOA dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoPOA created = service.save(dto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el POA.");
        }
    }

    /**
     * Actualiza un POA existente.
     *
     * @param id        ID del POA.
     * @param dto       Datos del POA.
     * @param authHeader Cabecera de autorización.
     * @return POA actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DtoPOA dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoPOA updated = service.update(id, dto, token);
            return ResponseEntity.ok(updated);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el POA.");
        }
    }

    /**
     * Elimina un POA por su ID.
     *
     * @param id        ID del POA.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta sin contenido si se elimina correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        if (id == null) {
            return badRequest("El id del POA no puede ser nulo.");
        }
        try {
            String token = extractToken(authHeader);
            service.delete(id, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el POA.");
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