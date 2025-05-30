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
@Table(name = "UZKTPRODINT", schema = SHEMA)
public class ProdInt {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPRODINT_ID")
    private Long id;

    @Column(name = "UZKTPRODINT_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTPROGINST_CODE_FK", length = 60)
    private String codigoProginstFk;

    @Column(name = "UZKTPRODINT_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTPRODINT_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTPRODINT_ALINEACION")
    private TipoAlineacion alineacion;

    @Column(name = "UZKTPRODINT_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPRODINT_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPRODINT_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPRODINT_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}
