package ec.edu.espe.plantillaEspe.dto;

import jakarta.persistence.EnumType;
import lombok.Data;
import java.util.List;
import java.util.Date;
import jakarta.persistence.Enumerated;

@Data
public class DtoCampo {
    private Long id;                  // Identificador único del campo
    private String codigo;            // Código interno o nombre técnico
    private String label;             // Texto visible para el usuario
    private String tipoCampo;         // Tipo de input: "text", "number", "select", "tabla", "file", etc.
    private Boolean requerido;        // Si el campo es obligatorio
    private Boolean soloLectura;      // Si el campo es solo de lectura
    private Boolean esMultiple;       // Si permite seleccionar varios valores (select, checkbox, etc.)
    private List<Object> opciones;    // Opciones posibles (para select, radio, checkbox)
    private List<Object> columnas;    // Nombres de columnas (para tablas)
    private List<Object> filas;       // Nombres de filas (para tablas de doble entrada)
    //entidad para configurar la tabla solo columnas, filas o ambas entonces hay tres casos no debe ser boolean

    @Enumerated(EnumType.STRING)
    private TipoConfiguracionTabla tipoConfiguracionTabla; // SOLO_COLUMNAS, SOLO_FILAS, COLUMNAS_Y_FILAS TABLA_PERSONALIZADA
    private Object estructuraTablaPersonalizada;

    private Boolean esMutable;        // Si la tabla permite agregar/eliminar filas/columnas
    private Integer minFilas;         // Mínimo de filas permitidas (para tablas)
    private Integer maxFilas;         // Máximo de filas permitidas (para tablas)
    private Integer minColumnas;      // Mínimo de columnas permitidas (para tablas)
    private Integer maxColumnas;      // Máximo de columnas permitidas (para tablas)
    @Enumerated(EnumType.STRING)
    private Estado estado;
        // Auditoría
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    // ...otros campos según tus necesidades futuras
}