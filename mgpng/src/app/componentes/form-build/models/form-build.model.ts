export interface TipoProyecto {
  id?: number;
  codigo?: string;
  nombre?: string;
  descripcion?: string;
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

export interface Campo {
  id?: number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  tipoCampo?: string;
  estado?: string;
  usuarioCreacion?: string;
  fechaCreacion?: string | Date;
  usuarioModificacion?: string;
  fechaModificacion?: string | Date;
  filaMatriz: number;
  columnaMatriz: number;
}
