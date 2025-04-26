package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTCAMPOS", schema = SHEMA)
public class Campo {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "UZKTCAMPOS_CODE", length = 60)
    private String codigo;

    @Column(name = "UZKTCAMPOS_ID")
    private Long id;

    @Column(name = "UZKTCAMPOS_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTCAMPOS_STATUS", length = 6)
    private String estado;

    @Column(name = "UZKTCAMPOS_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTCAMPOS_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @Column(name = "UZKTCAMPOS_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTCAMPOS_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;

    @OneToMany(mappedBy = "campo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SeccionCampo> seccionCampos = new HashSet<>();
}