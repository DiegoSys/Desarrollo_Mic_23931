package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "UZKTPRESNATURALEZA")
public class PresNaturaleza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPRESNATURALEZA_ID")
    private Long id;

    @Column(name = "UZKTPRESNATURALEZA_CODE", length = 60, nullable = false)
    private String codigo;

    @Column(name = "UZKTPRESNATURALEZA_NAME", length = 360)
    private String nombre;

    @Column(name = "UZKTPRESNATURALEZA_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTPRESNATURALEZA_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPRESNATURALEZA_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPRESNATURALEZA_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPRESNATURALEZA_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @Column(name = "UZKTPRESNATURALEZA_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTPRESNATURALEZA_ALINEACION")
    private TipoAlineacion alineacion;

    // Relación con PresGrupo de uno a muchos
    // Explicación: Una naturaleza puede estar en muchos grupos, pero un grupo solo puede tener una naturaleza
    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "presNaturaleza", fetch = FetchType.LAZY)
    private List<PresGrupo> presGrupo = new ArrayList<>();
}
