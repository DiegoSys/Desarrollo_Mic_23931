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
@Table(name = "UZKTOBJOPER", schema = SHEMA)
public class ObjOperativo {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTOBJOPER_ID")
    private Long id;

    @Column(name = "UZKTOBJOPER_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTOBJESTR_CODE_FK", length = 60)
    private String codigoEstrFk;

    @Column(name = "UZKTOBJOPER_DESC", length = 1000)
    private String descripcion;

    @Column(name = "UZKTOBJOPER_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTOBJOPER_ALINEACION")
    private TipoAlineacion alineacion;

    @Column(name = "UZKTOBJOPER_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTOBJOPER_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTOBJOPER_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTOBJOPER_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}