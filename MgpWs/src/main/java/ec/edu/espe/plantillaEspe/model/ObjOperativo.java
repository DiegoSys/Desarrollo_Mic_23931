package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTOBJOPER", schema = SHEMA)
public class ObjOperativo {
    private static final long serialVersionUID = 1L;

    @Column(name = "UZKTOBJOPER_ID")
    private Long id;

    @Id
    @Column(name = "UZKTOBJOPER_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTOBJESTR_CODE_FK", length = 60)
    private String codigoEstrFk;

    @Column(name = "UZKTOBJOPER_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTOBJOPER_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTOBJOPER_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTOBJOPER_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTOBJOPER_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}