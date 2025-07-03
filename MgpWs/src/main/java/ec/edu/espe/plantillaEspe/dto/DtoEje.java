package ec.edu.espe.plantillaEspe.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import java.util.Date;

/**
 * DTO para la entidad Eje.
 *
 * Este DTO representa la información de un eje estratégico, incluyendo sus datos principales,
 * campos de auditoría y referencias a la alineación y estado.
 *
 * Campos:
 * - id: Identificador único del eje.
 * - codigo: Código del eje.
 * - codigoPlanNacionalFk: Código del plan nacional relacionado.
 * - descripcion: Descripción del eje.
 * - estado: Estado del eje (activo/inactivo).
 * - alineacion: Tipo de alineación del eje.
 * - usuarioCreacion: Usuario que creó el registro.
 * - fechaCreacion: Fecha de creación del registro.
 * - usuarioModificacion: Usuario que modificó el registro.
 * - fechaModificacion: Fecha de la última modificación del registro.
 */

@Data
public class DtoEje {
    private Long id;
    private String codigo;
    private String codigoPlanNacionalFk;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    private TipoAlineacion alineacion;

    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
}