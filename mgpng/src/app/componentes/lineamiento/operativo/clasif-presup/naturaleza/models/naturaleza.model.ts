import { GrupoModel } from '@/app/componentes/lineamiento/operativo/clasif-presup/grupo/models/grupo.model';

export interface NaturalezaModel {
    codigo: string;
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    usuarioCreacion?: string;
    fechaCreacion?: Date;
    usuarioModificacion?: string | null;
    fechaModificacion?: Date | null;
    presGrupoList?: GrupoModel[];
}

export interface NaturalezaUpdateModel {
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    codigo?: string;
    presGrupoList?: GrupoModel[];
}

export interface TablePageEvent {
    first?: number;
    rows?: number;
}
