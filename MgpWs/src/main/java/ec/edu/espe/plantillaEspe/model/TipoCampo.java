package ec.edu.espe.plantillaEspe.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "UZKTTIPOCAMPO")
public class TipoCampo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "UZKTTIPOCAMPO_CODE", nullable = false, unique = true)
    private String codigo;

    @Column(name = "UZKTTIPOCAMPO_NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "UZKTTIPOCAMPO_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(name = "UZKTTIPOCAMPO_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @Column(name = "UZKTTIPOCAMPO_USER_CREA")
    private String usuarioCreacion;

    @Column(name = "UZKTTIPOCAMPO_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;

    @Column(name = "UZKTTIPOCAMPO_USER_MOD")
    private String usuarioModificacion;
    
}