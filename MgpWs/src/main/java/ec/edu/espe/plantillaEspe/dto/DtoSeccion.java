package ec.edu.espe.plantillaEspe.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DtoSeccion
{
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    private List<DtoSeccionCampo> campos = new ArrayList<>();
}
