export interface PnlModel {
  codigo: string;
  codigoMetaFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string | null;
  fechaModificacion?: Date | null;
}

export interface PnlUpdateModel {
  codigoMetaFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
  codigo?: string;
}

export interface TablePageEvent {
  first?: number;
  rows?: number;
}
