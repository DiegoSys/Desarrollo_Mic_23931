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
@Table(name = "UZKTOBJESTR", schema = SHEMA)
public class ObjEstrategico {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTOBJESTR_ID")
    private Long id;

    @Column(name = "UZKTOBJESTR_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTMETA_CODE_FK", length = 60)
    private String codigoMetaFk;

    @Column(name = "UZKTOBJESTR_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTOBJESTR_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTOBJESTR_ALINEACION")
    private TipoAlineacion alineacion;

    @Column(name = "UZKTOBJESTR_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTOBJESTR_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTOBJESTR_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTOBJESTR_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}