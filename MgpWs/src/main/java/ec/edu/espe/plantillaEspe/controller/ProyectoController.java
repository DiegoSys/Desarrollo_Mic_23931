package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoProyecto;
import ec.edu.espe.plantillaEspe.service.ServiceProyecto;
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
@RequestMapping(V1_API_VERSION+"/proyecto")
public class ProyectoController {

    @Autowired
    private ServiceProyecto serviceProyecto;

    @GetMapping("/{codigo}")
    public ResponseEntity<DtoProyecto> find(@PathVariable String codigo) {
        DtoProyecto proyecto = this.serviceProyecto.find(codigo);
        return new ResponseEntity<>(proyecto, HttpStatus.OK);
    }

    // Nuevo endpoint paginado
    @GetMapping
    public ResponseEntity<Page<DtoProyecto>> findAllPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(serviceProyecto.findAll(pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<DtoProyecto> save(@RequestBody DtoProyecto body) {
        DtoProyecto savedProyecto = this.serviceProyecto.save(body);
        return new ResponseEntity<>(savedProyecto, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<DtoProyecto> update(@RequestBody DtoProyecto body) {
        DtoProyecto updatedProyecto = this.serviceProyecto.update(body);
        if (updatedProyecto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedProyecto, HttpStatus.OK);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> delete(@PathVariable String codigo) {
        this.serviceProyecto.delete(codigo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}