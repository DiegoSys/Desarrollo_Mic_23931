package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTTIPOPROYEC_SECCION", schema = SHEMA)
public class ProyectoSeccion {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTTIPOPROYEC_SEC_ID")
    private Long id;

    @Column(name = "UZKTTIPOPROYEC_SEC_CODE", length = 60)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTTIPOPROYEC_CODE", referencedColumnName = "UZKTTIPOPROYEC_CODE")
    private TipoProyecto tipoProyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTSECCIONES_CODE", referencedColumnName = "UZKTSECCIONES_CODE")
    private Seccion seccion;

    @Column(name = "UZKTTIPOPROYEC_SEC_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTTIPOPROYEC_SEC_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @Column(name = "UZKTTIPOPROYEC_SEC_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTTIPOPROYEC_SEC_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;
}