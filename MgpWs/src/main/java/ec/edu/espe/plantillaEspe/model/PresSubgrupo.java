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
@Table(name = "UZKTPRESSUBGRUPO")
public class PresSubgrupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPRESSUBGRUPO_ID")
    private Long id;

    @Column(name = "UZKTPRESSUBGRUPO_CODE", length = 60, nullable = false)
    private String codigo;

    @Column(name = "UZKTPRESSUBGRUPO_NAME", length = 360)
    private String nombre;

    @Column(name = "UZKTPRESSUBGRUPO_DESC", length = 360)
    private String descripcion;

    @Column(name = "UZKTPRESSUBGRUPO_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPRESSUBGRUPO_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPRESSUBGRUPO_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPRESSUBGRUPO_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @Column(name = "UZKTPRESSUBGRUPO_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTPRESSUBGRUPO_ALINEACION")
    private TipoAlineacion alineacion;


    // Relación con PresGrupo de muchos a uno
    // Explicación: Un subgrupo puede pertenecer a un grupo, pero un grupo puede tener muchos subgrupos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTPRESGRUPO_CODE", referencedColumnName = "UZKTPRESGRUPO_CODE")
    private PresGrupo presGrupo;

    // Relación con PresItem de uno a muchos
    // Explicación: Un subgrupo puede tener muchos items, pero un item solo puede pertenecer a un subgrupo
    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "presSubgrupo", fetch = FetchType.LAZY)
    private List<PresItem> presItems = new ArrayList<>();
}
