package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTPROGNAC", schema = SHEMA)
public class ProgNac {
    private static final long serialVersionUID = 1L;

    @Column(name = "UZKTPROGNAC_ID")
    private Long id;

    @Id
    @Column(name = "UZKTPROGNAC_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTMETA_CODE_FK", length = 60)
    private String codigoMetaFk;

    @Column(name = "UZKTPROGNAC_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTPROGNAC_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPROGNAC_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPROGNAC_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPROGNAC_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}