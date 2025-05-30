package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DtoActividad {
    private Long id;
    private String codigo;
    private String descripcion;
    private String nombre;
    private String estado;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
}
