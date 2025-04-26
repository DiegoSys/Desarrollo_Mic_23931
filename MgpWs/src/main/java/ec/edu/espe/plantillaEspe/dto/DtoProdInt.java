package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;

@Data
public class DtoProdInt
{
    private Long id;
    private String codigo;
    private String codigoProginstFk;
    private String descripcion;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
}