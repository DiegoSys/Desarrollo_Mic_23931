package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;

@Data
public class DtoMatrizPoaConsulta {

    // Jerarquía de la Actividad
    private DtoPrograma programa;
    private DtoSubPrograma subprograma;
    private DtoProyecto proyecto;
    private DtoActividad actividad;

    // Jerarquía del Item
    private DtoPresNaturaleza presNaturaleza;
    private DtoPresGrupo presGrupo;
    private DtoPresSubgrupo presSubgrupo;
    private DtoPresItem Item;


    // Datos de la relación/intersección
    private Boolean existeRelacion;
    private String observacion;

}