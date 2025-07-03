package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoProdInt;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.IService.IServiceProdInt;
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
 * Controlador REST para la gestión de productos institucionales.
 * Proporciona endpoints para consultar, crear, actualizar y eliminar productos institucionales,
 * así como para obtener listados y paginación.
 *
 * Maneja validaciones y errores comunes, devolviendo respuestas adecuadas.
 *
 * @author ITS
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/prodint")
public class ProdIntController {

    private final IServiceProdInt serviceProdInt;

    @Autowired
    public ProdIntController(IServiceProdInt serviceProdInt) {
        this.serviceProdInt = serviceProdInt;
    }

    /**
     * Obtiene un producto institucional por su código.
     *
     * @param codigo Código del producto institucional.
     * @return El producto encontrado o un error si no existe.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del producto institucional no puede ser nulo o vacío.");
        }

        try {
            DtoProdInt prodInt = serviceProdInt.find(codigo);
            return ResponseEntity.ok(prodInt);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el producto institucional.");
        }
    }

    /**
     * Obtiene una lista de todos los productos institucionales activos.
     *
     * @return Lista de productos institucionales activos.
     */
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoProdInt> prodints = serviceProdInt.findAllActivos();
            return ResponseEntity.ok(prodints);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los productos intermedios.");
        }
    }

    /**
     * Obtiene una página de productos institucionales activos, con filtros opcionales.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param sort           Campo de ordenamiento.
     * @param direction      Dirección de ordenamiento (asc/desc).
     * @param searchCriteria Filtros de búsqueda adicionales.
     * @return Página de productos institucionales activos.
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
            Page<DtoProdInt> prodInts = serviceProdInt.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(prodInts);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los productos institucionales paginados.");
        }
    }

    /**
     * Crea un nuevo producto institucional.
     *
     * @param prodInt    Datos del producto institucional.
     * @param authHeader Cabecera de autorización.
     * @return Producto institucional creado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoProdInt prodInt,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoProdInt savedProdInt = serviceProdInt.save(prodInt, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProdInt);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el producto institucional.");
        }
    }

    /**
     * Actualiza un producto institucional existente.
     *
     * @param codigo     Código del producto institucional.
     * @param prodInt    Datos del producto institucional.
     * @param authHeader Cabecera de autorización.
     * @return Producto institucional actualizado.
     */
    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoProdInt prodInt,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            prodInt.setCodigo(codigo);
            DtoProdInt updatedProdInt = serviceProdInt.update(prodInt, token);
            return ResponseEntity.ok(updatedProdInt);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el producto institucional.");
        }
    }

    /**
     * Elimina un producto institucional por su código.
     *
     * @param codigo     Código del producto institucional.
     * @param authHeader Cabecera de autorización.
     * @return Respuesta sin contenido si se elimina correctamente.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
            @PathVariable String codigo,
            @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del producto institucional no puede ser nulo o vacío.");
        }

        try {
            String token = extractToken(authHeader);
            serviceProdInt.delete(codigo, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el producto institucional.");
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