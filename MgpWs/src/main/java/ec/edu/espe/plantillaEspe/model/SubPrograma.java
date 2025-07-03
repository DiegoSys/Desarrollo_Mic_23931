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
@Table(
    name = "UZKTSUBPROGRAMA",
    uniqueConstraints = @UniqueConstraint(columnNames = {
        "UZKTSUBPROGRAMA_CODE", 
        "UZKTPROGRAMA_ID"
    })
)
public class SubPrograma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTSUBPROGRAMA_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTPROGRAMA_ID", referencedColumnName = "UZKTPROGRAMA_ID", nullable = false)
    private Programa programa;

    @Column(name = "UZKTSUBPROGRAMA_CODE", length = 60, nullable = false)
    private String codigo;

    @Column(name = "UZKTSUBPROGRAMA_NAME", length = 360)
    private String nombre;

    @Column(name = "UZKTSUBPROGRAMA_DES", length = 360)
    private String descripcion;

    @Column(name = "UZKTSUBPROGRAMA_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTSUBPROGRAMA_ALINEACION")
    private TipoAlineacion alineacion;

    @Column(name = "UZKTSUBPROGRAMA_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTSUBPROGRAMA_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTSUBPROGRAMA_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTSUBPROGRAMA_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "subprograma", fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private List<Proyecto> proyectos = new ArrayList<>();
}