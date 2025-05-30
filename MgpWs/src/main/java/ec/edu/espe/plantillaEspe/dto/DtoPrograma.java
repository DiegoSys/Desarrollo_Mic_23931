package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DtoPrograma {
    private Long id;
    private String codigo;
    private String descripcion;
    private String nombre;
    private String estado;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    List<DtoSubPrograma> subProgramaList = new ArrayList<>();
}
