package ec.edu.espe.plantillaEspe.controller;

import ec.edu.espe.plantillaEspe.dto.DtoCampo;
import ec.edu.espe.plantillaEspe.dto.DtoSeccion;
import ec.edu.espe.plantillaEspe.service.ServiceCampo;
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
@RequestMapping(V1_API_VERSION+"/campo")
public class CampoController {

    @Autowired
    private ServiceCampo serviceCampo;

    @GetMapping("/{codigo}")
    public ResponseEntity<DtoCampo> find(@PathVariable String codigo) {
        DtoCampo campo = this.serviceCampo.find(codigo);
        return new ResponseEntity<>(campo, HttpStatus.OK);
    }

    // Nuevo endpoint paginado
    @GetMapping
    public ResponseEntity<Page<DtoCampo>> findAllPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDateA") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(serviceCampo.findAll(pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<DtoCampo> save(@RequestBody DtoCampo body) {
        DtoCampo savedCampo = this.serviceCampo.save(body);
        return new ResponseEntity<>(savedCampo, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<DtoCampo> update(@RequestBody DtoCampo body) {
        DtoCampo updatedCampo = this.serviceCampo.update(body);
        if (updatedCampo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedCampo, HttpStatus.OK);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> delete(@PathVariable String codigo) {
        this.serviceCampo.delete(codigo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}