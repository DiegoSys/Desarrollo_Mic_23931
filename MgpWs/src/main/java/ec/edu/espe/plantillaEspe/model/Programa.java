package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "UZKTPROGRAMA")
public class Programa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPROGRAMA_ID")
    private Long id;

    @Column(name = "UZKTPROGRAMA_CODE", length = 60, nullable = false)
    private String codigo;

    @Column(name = "UZKTPROGRAMA_NAME", length = 360)
    private String nombre;

    @Column(name = "UZKTPROGRAMA_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTPROGRAMA_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTPROGRAMA_ALINEACION")
    private TipoAlineacion alineacion;

    @Column(name = "UZKTPROGRAMA_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPROGRAMA_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPROGRAMA_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPROGRAMA_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    //relación con Subprograma
    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "programa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SubPrograma> subProgramas = new ArrayList<>();
    
}