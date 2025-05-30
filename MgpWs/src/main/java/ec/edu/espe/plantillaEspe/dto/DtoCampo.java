package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class DtoCampo {
    private Long id;
    private String codigo;
    private String descripcion;
    private String estado;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
}