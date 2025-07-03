package ec.edu.espe.plantillaEspe.dto;

import lombok.Data;
import java.util.Date;

/**
 * DTO para la entidad POA.
 * 
 * Este DTO se utiliza para transferir los datos de la entidad POA entre las capas de la aplicación.
 * Contiene referencias a los identificadores de Actividad e Item, así como los campos de auditoría.
 *
 * Campos:
 * - id: Identificador único del POA.
 * - estado: Estado del POA (activo/inactivo).
 * - actividadID: Identificador de la Actividad asociada.
 * - itemID: Identificador del Item asociado.
 * - usuarioCreacion: Usuario que creó el registro.
 * - fechaCreacion: Fecha de creación del registro.
 * - usuarioModificacion: Usuario que modificó el registro.
 * - fechaModificacion: Fecha de la última modificación del registro.
 */
@Data
public class DtoPOA {
    private Long id;
    private Estado estado;

    // Referencias a los IDs de Actividad e Item
    private Long actividadID;
    private Long itemID;

    // Auditoría
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
}
