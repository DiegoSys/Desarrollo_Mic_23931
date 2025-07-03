export interface ProdInsModel {
  codigo: string;
  codigoProginstFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string | null;
  fechaModificacion?: Date | null;
}

export interface ProdInsUpdateModel {
  codigoProginstFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
  codigo?: string;
}

export interface TablePageEvent {
  first?: number;
  rows?: number;
}
