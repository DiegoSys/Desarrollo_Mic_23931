package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoProyectoSeccion;
import ec.edu.espe.plantillaEspe.exception.DataValidationException;
import ec.edu.espe.plantillaEspe.exception.NotFoundException;
import ec.edu.espe.plantillaEspe.service.ServiceProyectoSeccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.V1_API_VERSION;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(V1_API_VERSION + "/proyecto-seccion")
public class ProyectoSeccionController {

    private final ServiceProyectoSeccion serviceProyectoSeccion;

    @Autowired
    public ProyectoSeccionController(ServiceProyectoSeccion serviceProyectoSeccion) {
        this.serviceProyectoSeccion = serviceProyectoSeccion;
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código de la relación proyecto-sección no puede ser nulo o vacío.");
        }

        try {
            DtoProyectoSeccion proyectoSeccion = serviceProyectoSeccion.find(codigo);
            return ResponseEntity.ok(proyectoSeccion);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al buscar la relación proyecto-sección.");
        }
    }

    @GetMapping("list")
    public ResponseEntity<?> findAll() {
        try {
            List<DtoProyectoSeccion> proyectoSecciones = serviceProyectoSeccion.findAll();
            return ResponseEntity.ok(proyectoSecciones);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener las relaciones proyecto-sección.");
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
            Page<DtoProyectoSeccion> proyectoSecciones = serviceProyectoSeccion.findAll(pageable);
            return ResponseEntity.ok(proyectoSecciones);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al obtener las relaciones proyecto-sección paginadas.");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> create(
            @RequestBody DtoProyectoSeccion proyectoSeccion,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            DtoProyectoSeccion savedProyectoSeccion = serviceProyectoSeccion.save(proyectoSeccion, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProyectoSeccion);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al crear la relación proyecto-sección."+e);
        }
    }

    @PutMapping("/update/{codigo}")
    public ResponseEntity<?> update(
            @PathVariable String codigo,
            @RequestBody DtoProyectoSeccion proyectoSeccion,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            proyectoSeccion.setCodigo(codigo);
            DtoProyectoSeccion updatedProyectoSeccion = serviceProyectoSeccion.update(proyectoSeccion, token);
            return ResponseEntity.ok(updatedProyectoSeccion);
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al actualizar la relación proyecto-sección.");
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(@PathVariable String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return badRequest("El código de la relación proyecto-sección no puede ser nulo o vacío.");
        }

        try {
            serviceProyectoSeccion.delete(codigo);
            return ResponseEntity.noContent().build();
        } catch (DataValidationException e) {
            return badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return notFound(e.getMessage());
        } catch (Exception e) {
            return internalServerError("Ocurrió un error interno al eliminar la relación proyecto-sección.");
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