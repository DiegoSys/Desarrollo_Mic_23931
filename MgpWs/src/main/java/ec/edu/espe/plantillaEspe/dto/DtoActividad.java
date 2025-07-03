package ec.edu.espe.plantillaEspe.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.Date;

/**
 * DTO para la entidad Actividad.
 *
 * Este DTO se utiliza para transferir los datos de la entidad Actividad entre las capas de la aplicación.
 * Incluye información básica de la actividad, auditoría y referencias a la jerarquía de proyecto, subprograma y programa.
 *
 * Campos:
 * - id: Identificador único de la actividad.
 * - codigo: Código de la actividad.
 * - descripcion: Descripción de la actividad.
 * - nombre: Nombre de la actividad.
 * - usuarioCreacion: Usuario que creó el registro.
 * - fechaCreacion: Fecha de creación del registro.
 * - usuarioModificacion: Usuario que modificó el registro.
 * - fechaModificacion: Fecha de la última modificación del registro.
 * - estado: Estado de la actividad (activo/inactivo).
 * - alineacion: Tipo de alineación de la actividad.
 * - proyectoId: Identificador del proyecto asociado.
 * - subProgramaId: Identificador del subprograma asociado.
 * - programaId: Identificador del programa asociado.
 */
@Data
public class DtoActividad {
    private Long id;
    private String codigo;
    private String descripcion;
    private String nombre;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    private TipoAlineacion alineacion;
    
    private Long proyectoId;
    private Long subProgramaId;
    private Long programaId;
}
