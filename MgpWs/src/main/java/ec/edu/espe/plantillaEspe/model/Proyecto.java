package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(
    name = "UZKTPROYECTO",
    uniqueConstraints = @UniqueConstraint(columnNames = {
        "UZKTPROYECTO_CODE", 
        "UZKTSUBPROGRAMA_ID",
        "UZKTPROGRAMA_ID"
    })
)
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPROYECTO_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTPROGRAMA_ID", referencedColumnName = "UZKTPROGRAMA_ID", nullable = false)
    private Programa programa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTSUBPROGRAMA_ID", referencedColumnName = "UZKTSUBPROGRAMA_ID", nullable = false)
    private SubPrograma subprograma;

    @Column(name = "UZKTPROYECTO_CODE", length = 60, nullable = false)
    private String codigo;

    @Column(name = "UZKTPROYECTO_NAME", length = 360)
    private String nombre;

    @Column(name = "UZKTPROYECTO_DESC", length = 360)
    private String descripcion;


    @Column(name = "UZKTPROYECTO_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTPROYECTO_ALINEACION")
    private TipoAlineacion alineacion;

    @Column(name = "UZKTPROYECTO_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPROYECTO_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPROYECTO_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPROYECTO_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @OneToMany(mappedBy = "proyecto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Actividad> actividades = new ArrayList<>();
}