import { ItemModel } from "./../../item/models/item.model";

export interface SubGrupoModel {
    codigo: string;
    codigoGrupoFk?: string;
    nombre: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    usuarioCreacion?: string;
    fechaCreacion?: Date;
    usuarioModificacion?: string | null;
    fechaModificacion?: Date | null;
    presItemList: ItemModel[];
}

export interface SubGrupoUpdateModel {
    nombre: string;
    codigoGrupoFk?: string;
    descripcion: string;
    estado?: string;
    alineacion?: string;
    codigo?: string;
    presItemList?: ItemModel[];
}

export interface TablePageEvent {
    first?: number;
    rows?: number;
}
