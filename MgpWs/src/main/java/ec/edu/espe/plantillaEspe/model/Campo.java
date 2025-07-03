package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.*;

import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTCAMPOS", schema = SHEMA)
public class Campo {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTCAMPOS_ID")
    private Long id;

    @Column(name = "UZKTCAMPOS_CODE", length = 60, unique = true)
    private String codigo;

    @Column(name = "UZKTCAMPOS_NOMBRE")
    private String nombre;

    @Column(name = "UZKTCAMPOS_DESC")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTCAMPOS_STATUS")
    private Estado estado;
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

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "campo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeccionCampo> seccionCampos = new ArrayList<>();

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTTIPOCAMPO_CODE", referencedColumnName = "UZKTTIPOCAMPO_CODE")
    private TipoCampo tipoCampo;

    @ToString.Exclude  // Excluir del toString para evitar recursión
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTMATRIZ_ID", referencedColumnName = "UZKTMATRIZ_ID")
    private Matriz matriz;
}