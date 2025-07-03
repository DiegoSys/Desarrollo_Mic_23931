import { SubProgramaModel } from "./../../subPrograma/models/subPrograma.model";

export interface ProgramaModel {
    id?: number;
    codigo: string;
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    usuarioCreacion?: string;
    fechaCreacion?: Date;
    usuarioModificacion?: string | null;
    fechaModificacion?: Date | null;
    subProgramaList?: SubProgramaModel[];
}

export interface ProgramaUpdateModel {
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    codigo: string;
    subProgramaList?: SubProgramaModel[];
}

export interface TablePageEvent {
    first?: number;
    rows?: number;
}
