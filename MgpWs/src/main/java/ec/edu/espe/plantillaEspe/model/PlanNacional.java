package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTPLANNACIONAL", schema = SHEMA)
public class PlanNacional {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPLANNACIONAL_ID")
    private Long id;

    @Column(name = "UZKTPLANNACIONAL_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTOBJDESSOST_CODE_FK", length = 60)
    private String codigoObjDessostFk;

    @Column(name = "UZKTPLANNACIONAL_DESC", length = 320)
    private String descripcion;

    @Column(name = "UZKTPLANNACIONAL_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTPLANNACIONAL_ALINEACION")
    private TipoAlineacion alineacion;

    @Column(name = "UZKTPLANNACIONAL_FEC_INI")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Column(name = "UZKTPLANNACIONAL_FEC_FIN")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    @Column(name = "UZKTPLANNACIONAL_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPLANNACIONAL_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPLANNACIONAL_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPLANNACIONAL_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = "America/Guayaquil")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}