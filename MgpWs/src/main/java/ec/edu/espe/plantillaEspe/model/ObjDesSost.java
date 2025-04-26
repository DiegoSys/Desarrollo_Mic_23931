package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTOBJDESSOST", schema = SHEMA)
public class ObjDesSost {
    private static final long serialVersionUID = 1L;

    @Column(name = "UZKTOBJDESSOST_ID")
    private Long id;

    @Id
    @Column(name = "UZKTOBJDESSOST_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTOBJDESSOST_DESC", length = 120)
    private String descripcion;

    @Column(name = "UZKTOBJDESSOST_STATUS", length = 2)
    private String estado;

    @Column(name = "UZKTOBJDESSOST_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTOBJDESSOST_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTOBJDESSOST_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTOBJDESSOST_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}