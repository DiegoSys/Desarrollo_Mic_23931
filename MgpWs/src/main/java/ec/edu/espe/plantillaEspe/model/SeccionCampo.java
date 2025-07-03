package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTSECCIONES_CAMPOS", schema = SHEMA)
public class SeccionCampo {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTSECCIONES_CAM_ID")
    private Long id;

    @Column(name = "UZKTSECCIONES_CAM_CODE", length = 60)
    private String codigo;

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTSECCIONES_CODE", referencedColumnName = "UZKTSECCIONES_CODE")
    private Seccion seccion;

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTCAMPOS_CODE", referencedColumnName = "UZKTCAMPOS_CODE")
    private Campo campo;

    @Column(name = "UUZKTSECCIONES_CAM_ORDEN")
    private Long orden;

    @Column(name = "UZKTSECCIONES_CAM_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTSECCIONES_CAM_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @Column(name = "UZKTSECCIONES_CAM_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTSECCIONES_CAM_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;
}