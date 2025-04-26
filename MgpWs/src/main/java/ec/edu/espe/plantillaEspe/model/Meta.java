package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTMETA", schema = SHEMA)
public class Meta {
    private static final long serialVersionUID = 1L;

    @Column(name = "UZKTMETA_ID")
    private Long id;

    @Id
    @Column(name = "UZKTMETA_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTOPN_CODE_FK", length = 60)
    private String codigoOpnFk;

    @Column(name = "UZKTMETA_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTMETA_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTMETA_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTMETA_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTMETA_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}