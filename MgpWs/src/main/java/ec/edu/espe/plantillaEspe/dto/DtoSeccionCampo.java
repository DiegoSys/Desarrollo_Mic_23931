package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;

@Data
public class DtoSeccionCampo
{
    private Long id;
    private String codigo;
    private Long orden;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    private String codigoSeccionFk;
    private String codigoCampoFk;
}