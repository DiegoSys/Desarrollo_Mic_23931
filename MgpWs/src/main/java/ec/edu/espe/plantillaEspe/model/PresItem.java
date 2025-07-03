package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@Entity
@Table(name = "UZKTPRESITEM")
public class PresItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPRESITEM_ID")
    private Long id;

    @Column(name = "UZKTPRESITEM_CODE", length = 60, nullable = false)
    private String codigo;

    @Column(name = "UZKTPRESITEM_NAME", length = 360)
    private String nombre;

    @Column(name = "UZKTPRESITEM_DESC", length = 1000)
    private String descripcion;

    @Column(name = "UZKTPRESITEM_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPRESITEM_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPRESITEM_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPRESITEM_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @Column(name = "UZKTPRESITEM_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTPRESITEM_ALINEACION")
    private TipoAlineacion alineacion;


    // Relación con PresSubgrupo de muchos a uno
    // Explicación: Un item puede pertenecer a un subgrupo, pero un subgrupo puede tener muchos items
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTPRESSUBGRUPO_CODE", referencedColumnName = "UZKTPRESSUBGRUPO_CODE")
    private PresSubgrupo presSubgrupo;
}