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
@Table(name = "UZKTSECCIONES", schema = SHEMA)
public class Seccion {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "UZKTSECCIONES_CODE")
    private String codigo;

    @Column(name = "UZKTSECCIONES_ID")
    private Long id;

    @Column(name = "UZKTSECCIONES_DESC", nullable = false)
    private String descripcion;

    @Column(name = "UZKTSECCIONES_STATUS", length = 6)
    private String estado;

    @Column(name = "UZKTSECCIONES_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date creationDateA;

    @Column(name = "UZKTSECCIONES_USER_CREA")
    private String creationUser;

    @Column(name = "UZKTSECCIONES_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date modificationDate;

    @Column(name = "UZKTSECCIONES_USER_MOD")
    private String modificationUser;

    @OneToMany(mappedBy = "seccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SeccionCampo> seccionCampos = new HashSet<>();

    @OneToMany(mappedBy = "seccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProyectoSeccion> proyectoSecciones = new HashSet<>();
}
