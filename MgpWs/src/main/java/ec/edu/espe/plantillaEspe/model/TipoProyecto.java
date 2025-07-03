package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import ec.edu.espe.plantillaEspe.dto.Estado;

import java.util.*;

@Data
@Entity
@Table(name = "UZKTTIPOPROYEC") 
public class TipoProyecto {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTTIPOPROYEC_ID")
    private Long id;

    @Column(name = "UZKTTIPOPROYEC_CODE", nullable = false, unique = true)
    private String codigo;



    @Column(name = "UZKTTIPOPROYEC_NOMBRE")
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTTIPOPROYEC_STATUS")
    private Estado estado;
    
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

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "tipoProyecto", fetch = FetchType.LAZY)
    private List<ProyectoSeccion> proyectoSecciones = new ArrayList<>();

}