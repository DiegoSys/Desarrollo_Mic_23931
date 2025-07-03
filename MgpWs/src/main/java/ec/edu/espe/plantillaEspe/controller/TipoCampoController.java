package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoTipoCampo;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.ServiceTipoCampo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.V1_API_VERSION;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/tipo-campo")
public class TipoCampoController {

    private final ServiceTipoCampo serviceTipoCampo;

    @Autowired
    public TipoCampoController(ServiceTipoCampo serviceTipoCampo) {
        this.serviceTipoCampo = serviceTipoCampo;
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del tipo de campo no puede ser nulo o vacío.");
        }

        try {
            DtoTipoCampo tipoCampo = serviceTipoCampo.find(codigo);
            return ResponseEntity.ok(tipoCampo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar el tipo de campo.");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoTipoCampo> tiposCampo = serviceTipoCampo.findAll();
            return ResponseEntity.ok(tiposCampo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los tipos de campo.");
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<?> findAllActivos() {
        try {
            List<DtoTipoCampo> tiposCampoActivos = serviceTipoCampo.findAllActivos();
            return ResponseEntity.ok(tiposCampoActivos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los tipos de campo activos.");
        }
    }

    @GetMapping
    public ResponseEntity<?> findAllPaginado(
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
            Page<DtoTipoCampo> tiposCampo = serviceTipoCampo.findAll(pageable);
            return ResponseEntity.ok(tiposCampo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los tipos de campo paginados.");
        }
    }

    /**
     * Obtiene todos los tipos de campo activos con paginación y filtros de búsqueda.
     *
     * @param page      Número de página.
     * @param size      Tamaño de página.
     * @param sort      Campo de ordenamiento.
     * @param direction Dirección de ordenamiento (asc/desc).
     * @param codigo    Filtro por código (opcional).
     * @param nombre    Filtro por nombre (opcional).
     * @return Página de tipos de campo activos.
     */
    @GetMapping("/activos/paginado")
    public ResponseEntity<?> findAllActivosPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String nombre) {

        if (page < 0) {
            return badRequest("El número de página no puede ser negativo.");
        }

        if (size <= 0) {
            return badRequest("El tamaño de página debe ser mayor a cero.");
        }

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Map<String, String> searchCriteria = new HashMap<>();
            if (codigo != null && !codigo.isEmpty()) {
                searchCriteria.put("codigo", codigo);
            }
            if (nombre != null && !nombre.isEmpty()) {
                searchCriteria.put("nombre", nombre);
            }

            Page<DtoTipoCampo> tiposCampoActivos = serviceTipoCampo.findAllActivos(pageable, searchCriteria);
            return ResponseEntity.ok(tiposCampoActivos);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener los tipos de campo activos paginados.");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoTipoCampo tipoCampo,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoTipoCampo savedTipoCampo = serviceTipoCampo.save(tipoCampo, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTipoCampo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear el tipo de campo.");
        }
    }

    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoTipoCampo tipoCampo,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            tipoCampo.setCodigo(codigo);
            DtoTipoCampo updatedTipoCampo = serviceTipoCampo.update(tipoCampo, token);
            return ResponseEntity.ok(updatedTipoCampo);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar el tipo de campo.");
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(
            @PathVariable String codigo,
            @RequestHeader("Authorization") String authHeader) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código del tipo de campo no puede ser nulo o vacío.");
        }

        try {
            String token = extractToken(authHeader);
            serviceTipoCampo.delete(codigo, token);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar el tipo de campo.");
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