package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTOPN", schema = SHEMA)
public class OPN {
    private static final long serialVersionUID = 1L;

    @Column(name = "UZKTOPN_ID")
    private Long id;

    @Id
    @Column(name = "UZKTOPN_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTPLANNACIONAL_CODE_FK", length = 60)
    private String codigoPlanNacionalFk;

    @Column(name = "UZKTOPN_DESC", length = 60)
    private String descripcion;

    @Column(name = "UZKTOPN_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTOPN_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTOPN_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTOPN_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}