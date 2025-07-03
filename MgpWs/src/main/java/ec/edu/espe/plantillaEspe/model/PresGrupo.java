package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
import ec.edu.espe.plantillaEspe.dto.TipoAlineacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "UZKTPRESGRUPO")
public class PresGrupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPRESGRUPO_ID")
    private Long id;

    @Column(name = "UZKTPRESGRUPO_CODE", length = 60, nullable = false)
    private String codigo;

    @Column(name = "UZKTPRESGRUPO_NAME", length = 360)
    private String nombre;

    @Column(name = "UZKTPRESGRUPO_DESC", length = 1000)
    private String descripcion;

    @Column(name = "UZKTPRESGRUPO_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPRESGRUPO_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPRESGRUPO_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPRESGRUPO_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @Column(name = "UZKTPRESGRUPO_STATUS", length = 6)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "UZKTPRESGRUPO_ALINEACION")
    private TipoAlineacion alineacion;

    //Relacion con PresNaturaleza de muchos a uno
    //Explicacion: Un grupo puede tener una naturaleza, pero una naturaleza puede estar en muchos grupos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTPRESNATURALEZA_CODE", referencedColumnName = "UZKTPRESNATURALEZA_CODE")
    private PresNaturaleza presNaturaleza;

    //Relacion con PresSubgrupo de uno a muchos
    //Explicacion: Un grupo puede tener muchos subgrupos, pero un subgrupo solo puede pertenecer a un grupo
    @ToString.Exclude  // Excluir del toString para evitar recursión
    @OneToMany(mappedBy = "presGrupo", fetch = FetchType.LAZY)
    private List<PresSubgrupo> presSubgrupos = new ArrayList<>();
}
