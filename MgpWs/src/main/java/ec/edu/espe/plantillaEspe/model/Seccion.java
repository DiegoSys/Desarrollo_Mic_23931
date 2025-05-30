package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.ToString;

import java.util.*;

import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTSECCIONES", schema = SHEMA)
public class Seccion {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTSECCIONES_ID")
    private Long id;

    @Column(name = "UZKTSECCIONES_CODE", unique = true)
    private String codigo;

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

    @OneToMany(mappedBy = "seccion", fetch = FetchType.LAZY)
    List<SeccionCampo> seccionCampos;

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "seccion", fetch = FetchType.LAZY)
    List<ProyectoSeccion> proyectoSecciones = new ArrayList<>();


}
