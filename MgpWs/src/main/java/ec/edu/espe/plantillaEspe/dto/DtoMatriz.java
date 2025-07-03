package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DtoMatriz {
    private Long id;
    private Long fila;
    private Long columna;

    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
}
