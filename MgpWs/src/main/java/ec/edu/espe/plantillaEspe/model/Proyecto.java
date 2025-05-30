package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "UZKTPROYECTO")
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPROYECTO_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTSUBPROGRAMA_ID")
    private SubPrograma subprograma;

    @Column(name = "UZKTPROYECTO_CODE", length = 60, nullable = false)
    private String codigo;

    @Column(name = "UZKTPROYECTO_NAME", length = 360)
    private String nombre;

    @Column(name = "UZKTPROYECTO_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTPROYECTO_STATUS", length = 6)
    private String estado;

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

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    private List<Actividad> actividades = new ArrayList<>();
}