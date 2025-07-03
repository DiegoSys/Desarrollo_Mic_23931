export interface OpnModel {
  codigo: string;
  codigoPlanNacionalFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string | null;
  fechaModificacion?: Date | null;
}

export interface OpnUpdateModel {
  codigoPlanNacionalFk: string;
  descripcion: string;
  estado?: string;
  alineacion?: string;
}

export interface TablePageEvent {
  first?: number;
  rows?: number;
}
