package ec.edu.espe.plantillaEspe.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import ec.edu.espe.plantillaEspe.dto.Estado;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

import static ec.edu.espe.plantillaEspe.constant.GlobalConstants.SHEMA;

@Data
@Entity
@Table(name = "UZKTPOA", schema = SHEMA)
public class POA {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UZKTPOA_ID")
    private Long id;

    @Column(name = "UZKTPOA_STATUS", length = 6)
    private Estado estado;


    //Relcaion con la entidad acitivdad e Item  entidades hijas que tienen refrecnias de sus entidades padres
    //la matriz POA se compone de actividades e Items, por lo que se relaciona con estas entidades
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTPOA_ACTIVIDAD_ID", referencedColumnName = "UZKTACTIVIDAD_ID")
    private Actividad actividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UZKTPOA_ITEM_ID", referencedColumnName = "UZKTPRESITEM_ID")
    private PresItem item;
    


    //Data para auditoria
    @Column(name = "UZKTPOA_USER_CREA", length = 60)
    private String usuarioCreacion;

    @Column(name = "UZKTPOA_FEC_CREA")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "UZKTPOA_USER_MOD", length = 60)
    private String usuarioModificacion;

    @Column(name = "UZKTPOA_FEC_MOD")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = JsonFormat.DEFAULT_TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;





}

