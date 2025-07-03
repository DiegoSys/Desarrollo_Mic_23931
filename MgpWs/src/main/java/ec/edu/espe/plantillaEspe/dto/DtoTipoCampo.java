package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class DtoTipoCampo {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Estado estado;
    private Date fechaCreacion;
    private String usuarioCreacion;
    private Date fechaModificacion;
    private String usuarioModificacion;
}