import { ProyectoModel } from "../../proyecto/models/proyecto.model";

export interface SubProgramaModel {
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
    programaId?: number;
    proyectos: ProyectoModel[];
}

export interface SubProgramaUpdateModel {
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    codigo: string;
    programaId?: number;
    proyectos?: ProyectoModel[];
}

export interface TablePageEvent {
    first?: number;
    rows?: number;
}
