import { ActividadModel } from "../../actividad/models/actividad.model";

export interface ProyectoModel {
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
    subProgramaId?: number;
    programaId?: number;
    actividades?: ActividadModel[];
}

export interface ProyectoUpdateModel {
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    codigo: string;
    subProgramaId?: number;
    programaId?: number;
    actividades?: ActividadModel[];
}

export interface TablePageEvent {
    first?: number;
    rows?: number;
}
