package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTPRODINT", schema = SHEMA)
public class ProdInt {
    private static final long serialVersionUID = 1L;

    @Column(name = "UZKTPRODINT_ID")
    private Long id;

    @Id
    @Column(name = "UZKTPRODINT_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTPROGINST_CODE_FK", length = 60)
    private String codigoProginstFk;

    @Column(name = "UZKTPRODINT_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTPRODINT_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPRODINT_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPRODINT_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPRODINT_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}