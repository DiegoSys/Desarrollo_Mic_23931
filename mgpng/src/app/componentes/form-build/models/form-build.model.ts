export interface TipoProyecto {
  id?: number;
  codigo?: string;
  nombre?: string;
  estado?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  secciones: ProyectoSeccion[];
}

export interface ProyectoSeccion {
  id?: number;
  codigo?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  codigoProyectoFK?: string;
  codigoSeccionFk?: string;
  orden?: number;
  seccion?: Seccion; // Datos completos de la sección (opcional)
}

export interface Seccion {
  id?: number;
  codigo?: string;
  nombre?: string;
  descripcion?: string;
  estado?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  orden?: number;
  campos: SeccionCampo[];
}

export interface SeccionCampo {
  id?: number;
  codigo?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  codigoSeccionFk?: string;
  codigoCampoFk?: string;
  campo?: Campo; // Datos completos del campo (opcional)
}

export interface DtoDatoCampo {
  label: string;
  value: any;
}

export interface ColumnaTabla {
  nombre: string;
  tipo: 'input' | 'label';
}

export interface FilaTabla {
  nombre: string;
  tipo: 'input' | 'label';
}

// Nueva interfaz para tablas personalizadas
export interface CeldaPersonalizada {
  fila: number;
  columna: number;
  tipo: 'input' | 'label' | 'header';
  valor?: string;
  esEditable?: boolean;
  clase?: string; // Para aplicar estilos especiales
}

export interface EstructuraTablaPersonalizada {
  filas: FilaPersonalizada[];
}

export interface FilaPersonalizada {
  celdas: CeldaPersonalizada[];
}

export interface Campo {
  id?: number;
  codigo?: string;                  // Código que se genera en servidor
  label: string;                    // Texto visible para el usuario
  tipoCampo: string;   
  orden?: number; // <- Agrega esto  // el pk del campo relacionado
  requerido?: boolean;              // Si el campo es obligatorio
  soloLectura?: boolean;            // Si el campo es solo de lectura
  esMultiple?: boolean;             // Si permite seleccionar varios valores (select, checkbox, etc.)
  opciones?: string[];              // Opciones posibles (para select, radio, checkbox)
  columnas?: ColumnaTabla[]; // Cambiado de string[] a ColumnaTabla[]
  filas?: FilaTabla[];                 // Nombres de filas (para tablas de doble entrada)
  tipoConfiguracionTabla?: string;     // Tipo de tabla Solo FIlA / COlUMNA o AMABAS
  esMutable?: boolean;              // Si la tabla permite agregar/eliminar filas/columnas
  minFilas?: number;                // Mínimo de filas permitidas (para tablas)
  maxFilas?: number;     
  minColumnas?: number;      // Mínimo de columnas permitidas (para tablas)
  maxColumnas?: number;               // Máximo de filas permitidas (para tablas)
  estructuraTablaPersonalizada?: EstructuraTablaPersonalizada; // Para tablas personalizadas
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
}
