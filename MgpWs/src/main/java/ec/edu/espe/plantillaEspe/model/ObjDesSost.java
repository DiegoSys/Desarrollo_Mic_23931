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
@Table(name = "UZKTOBJDESSOST", schema = SHEMA)
public class ObjDesSost {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTOBJDESSOST_ID")
    private Long id;

    @Column(name = "UZKTOBJDESSOST_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTOBJDESSOST_DESC", length = 320)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTOBJDESSOST_STATUS", length = 2)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTOBJDESSOST_ALINEACION")
    private TipoAlineacion alineacion;

    @Column(name = "UZKTOBJDESSOST_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTOBJDESSOST_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTOBJDESSOST_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTOBJDESSOST_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}