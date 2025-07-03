package ec.edu.espe.plantillaEspe.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "UZKTMATRIZ")
public class Matriz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTMATRIZ_ID")
    private Long id;

    @Column(name = "UZKTMATRIZ_FILA", nullable = false)
    private Long fila;

    @Column(name = "UZKTMATRIZ_COLUMNA", nullable = false)
    private Long columna;

    @Column(name = "UZKTMATRIZ_USER_CREA")
    private String usuarioCreacion;

    @Column(name = "UZKTMATRIZ_FEC_CREA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTMATRIZ_USER_MOD")
    private String usuarioModificacion;

    @Column(name = "UZKTMATRIZ_FEC_MOD")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
}
