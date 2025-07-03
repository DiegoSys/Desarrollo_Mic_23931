export interface ActividadModel {
    id?: number;
    codigo: string;
    descripcion: string;
    nombre: string;
    usuarioCreacion?: string;
    fechaCreacion?: Date;
    usuarioModificacion?: string | null;
    fechaModificacion?: Date | null;
    estado?: string;
    alineacion?: string;
    proyectoId?: number;
    subProgramaId?: number;
    programaId?: number;
}

export interface ActividadUpdateModel {
    codigo: string;
    descripcion: string;
    nombre: string;
    usuarioCreacion?: string;
    fechaCreacion?: Date;
    usuarioModificacion?: string | null;
    fechaModificacion?: Date | null;
    estado?: string;
    alineacion?: string;
    proyectoId?: number;
    subProgramaId?: number;
    programaId?: number;
}

export interface TablePageEvent {
    first?: number;
    rows?: number;
}