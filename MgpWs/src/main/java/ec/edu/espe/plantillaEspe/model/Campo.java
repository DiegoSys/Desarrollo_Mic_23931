package ec.edu.espe.plantillaEspe.model;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoConfiguracionTabla;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "UZKTCAMPOS", schema = ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA)
public class Campo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTCAMPOS_ID")
    private Long id;

    @Column(name = "UZKTCAMPOS_CODE", length = 60, unique = true)
    private String codigo;

    @Column(name = "UZKTCAMPOS_LABEL")
    private String label;

    @Column(name = "UZKTCAMPOS_REQUERIDO")
    private Boolean requerido;

    @Column(name = "UZKTCAMPOS_SOLO_LECTURA")
    private Boolean soloLectura;

    @Column(name = "UZKTCAMPOS_MULTIPLE")
    private Boolean esMultiple;

    @Lob
    @Column(name = "UZKTCAMPOS_OPCIONES")
    private String opciones;

    @Lob
    @Column(name = "UZKTCAMPOS_COLUMNAS")
    private String columnas;

    @Lob
    @Column(name = "UZKTCAMPOS_FILAS")
    private String filas;

    @Lob
    @Column(name = "UZKTCAMPOS_EST_PERS")
    private String estructuraTablaPersonalizada;

    @Column(name = "UZKTCAMPOS_MUTABLE")
    private Boolean esMutable;

    @Column(name = "UZKTCAMPOS_MIN_FILAS")
    private Integer minFilas;

    @Column(name = "UZKTCAMPOS_MAX_FILAS")
    private Integer maxFilas;

    @Column(name = "UZKTCAMPOS_MIN_COLUMNAS")
    private Integer minColumnas;

    @Column(name = "UZKTCAMPOS_MAX_COLUMNAS")
    private Integer maxColumnas;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTCAMPOS_TIPO_CONFIG_TABLA")
    private TipoConfiguracionTabla tipoConfiguracionTabla;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTCAMPOS_STATUS")
    private Estado estado;

    @Column(name = "UZKTCAMPOS_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTCAMPOS_FEC_CREA")
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @Column(name = "UZKTCAMPOS_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTCAMPOS_FEC_MOD")
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTTIPOCAMPO_CODE", referencedColumnName = "UZKTTIPOCAMPO_CODE")
    private TipoCampo tipoCampo;

    
}