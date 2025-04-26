package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data
public class DtoProyecto
{
    private Long id;
    private String codigo;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    private Set<DtoProyectoSeccion> Secciones;
}