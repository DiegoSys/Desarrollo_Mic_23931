package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "UZKTTIPOPROYEC")
public class Proyecto {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "UZKTTIPOPROYEC_CODE", nullable = false, unique = true)
    private String codigo;

    @Column(name = "UZKTTIPOPROYEC_ID", nullable = false, unique = true)
    private Long id;

    @Column(name = "UZKTTIPOPROYEC_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @Column(name = "UZKTTIPOPROYEC_USER_CREA")
    private String usuarioCreacion;

    @Column(name = "UZKTTIPOPROYEC_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;

    @Column(name = "UZKTTIPOPROYEC_USER_MOD")
    private String usuarioModificacion;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProyectoSeccion> proyectoSecciones = new HashSet<>();
}