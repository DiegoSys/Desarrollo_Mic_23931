export interface ItemModel {
    codigo: string;
    codigoSubGrupoFk?: string;
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    usuarioCreacion?: string;
    fechaCreacion?: Date;
    usuarioModificacion?: string | null;
    fechaModificacion?: Date | null;
}

export interface ItemUpdateModel {
    nombre: string;
    codigoSubGrupoFk: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    codigo?: string;
}

export interface TablePageEvent {
    first?: number;
    rows?: number;
}
