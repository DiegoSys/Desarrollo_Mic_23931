package ec.edu.espe.plantillaEspe.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DtoPrograma {
    private Long id;
    private String codigo;
    private String descripcion;
    private String nombre;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    private TipoAlineacion alineacion;

    List<DtoSubPrograma> subProgramaList = new ArrayList<>();
}
