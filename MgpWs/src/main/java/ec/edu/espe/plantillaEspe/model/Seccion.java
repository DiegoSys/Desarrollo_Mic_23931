package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
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

    @Column(name = "UZKTSECCIONES_NOMBRE")
    private String nombre;

    @Column(name = "UZKTSECCIONES_DESC")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTSECCIONES_STATUS")
    private Estado estado;

    @Column(name = "UZKTSECCIONES_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @Column(name = "UZKTSECCIONES_USER_CREA")
    private String usuarioCreacion;

    @Column(name = "UZKTSECCIONES_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;

    @Column(name = "UZKTSECCIONES_USER_MOD")
    private String usuarioModificacion;

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "seccion", fetch = FetchType.LAZY)
    List<SeccionCampo> seccionCampos;

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "seccion", fetch = FetchType.LAZY)
    List<ProyectoSeccion> proyectoSecciones = new ArrayList<>();


}
