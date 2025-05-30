package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "UZKTACTIVIDAD")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTACTIVIDAD_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTPROYECTO_ID")
    private Proyecto proyecto;

    @Column(name = "UZKTACTIVIDAD_CODE", length = 60, nullable = false)
    private String codigo;

    @Column(name = "UZKTACTIVIDAD_NAME", length = 360)
    private String nombre;

    @Column(name = "UZKTACTIVIDAD_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTACTIVIDAD_STATUS", length = 6)
    private String estado;

    @Column(name = "UZKTACTIVIDAD_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTACTIVIDAD_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTACTIVIDAD_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTACTIVIDAD_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}