export interface PnModel {
  codigo: string;
  codigoObjDessostFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
  fechaInicio: Date;
  fechaFin: Date;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string | null;
  fechaModificacion?: Date | null;
}

export interface PnUpdateModel {
  codigoObjDessostFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
  fechaInicio: Date;
  fechaFin: Date;
}

export interface TablePageEvent {
  first?: number;
  rows?: number;
  page?: number;
  sortField?: string;
  sortOrder?: string;

}
