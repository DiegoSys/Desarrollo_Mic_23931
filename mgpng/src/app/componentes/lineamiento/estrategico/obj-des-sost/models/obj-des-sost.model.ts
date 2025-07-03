export interface OdsModel {
  codigo: string;
  descripcion: string;
  estado: string;
  alineacion?: string;
  fechaCreacion?: Date;
  usuarioCreacion?: string;
  fechaModificacion?: Date;
  usuarioModificacion?: string;
}

export interface OdsUpdateModel {
  descripcion: string;
  estado: string;
  alineacion?: string;
}

export interface TablePageEvent {
  first?: number;
  rows?: number;
  content?: OdsModel[];
  totalElements?: number;
}
