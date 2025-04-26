package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data
public class DtoSeccionCampo
{
    private Long id;
    private String codigo;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    private String codigoSeccion; // UZKTSECCIONES_CODE (FK)
    private String codigoCampo; // UZKTCAMPOS_CODE (FK)
}