package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;

@Data
public class DtoProyectoSeccion
{
    private Long id;
    private String codigo;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    private String codigoProyectoFK;
    private String codigoSeccionFk;
}