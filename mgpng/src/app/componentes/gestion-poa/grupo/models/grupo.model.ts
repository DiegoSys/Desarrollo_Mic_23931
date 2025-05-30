import { SubGrupoModel } from "../../subGrupo/models/subGrupo.model";

export interface GrupoModel {
    codigo: string;
    //codido de naturaleza
    codigoNaturalezaFk?: string;
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    usuarioCreacion?: string;
    fechaCreacion?: Date;
    usuarioModificacion?: string | null;
    fechaModificacion?: Date | null;
    presSubGrupoList: SubGrupoModel[];
}

export interface GrupoUpdateModel {
    codigoNaturalezaFk?: string;
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    codigo?: string;
    presSubGrupoList?: SubGrupoModel[];
}

export interface TablePageEvent {
    first?: number;
    rows?: number;
}
