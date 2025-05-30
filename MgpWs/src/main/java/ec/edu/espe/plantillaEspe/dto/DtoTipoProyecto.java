package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DtoTipoProyecto
{
    private Long id;
    private String codigo;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    private List<DtoProyectoSeccion> secciones = new ArrayList<>();
}