package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data
public class DtoProyectoSeccion
{
    private Long id;
    private String codigo;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    private String codigoTipoProyecto; // UZKTTIPOPROYEC_CODE (FK)
    private String codigoSeccion; // UZKTSECCIONES_CODE (FK)
}