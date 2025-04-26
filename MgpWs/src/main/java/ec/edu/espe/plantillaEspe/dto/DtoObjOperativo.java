package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;

@Data
public class DtoObjOperativo
{
    private Long id;
    private String codigo;
    private String codigoEstrFk;
    private String descripcion;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
}