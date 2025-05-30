export interface PdnModel {
  codigo: string;
  codigoOpnFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string | null;
  fechaModificacion?: Date | null;
}

export interface PdnUpdateModel {
  codigoOpnFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
}

export interface TablePageEvent {
  first?: number;
  rows?: number;
}