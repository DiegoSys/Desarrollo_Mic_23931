import { ProgramaModel } from 'src/app/componentes/lineamiento/operativo/estruct-presup/programa/models/programa.model';
import { SubProgramaModel } from 'src/app/componentes/lineamiento/operativo/estruct-presup/subPrograma/models/subPrograma.model';
import { ProyectoModel } from 'src/app/componentes/lineamiento/operativo/estruct-presup/proyecto/models/proyecto.model';
import { ActividadModel } from 'src/app/componentes/lineamiento/operativo/estruct-presup/actividad/models/actividad.model';
import { NaturalezaModel } from 'src/app/componentes/lineamiento/operativo/clasif-presup/naturaleza/models/naturaleza.model';
import { GrupoModel } from 'src/app/componentes/lineamiento/operativo/clasif-presup/grupo/models/grupo.model';
import { SubGrupoModel } from 'src/app/componentes/lineamiento/operativo/clasif-presup/subGrupo/models/subGrupo.model';
import { ItemModel } from 'src/app/componentes/lineamiento/operativo/clasif-presup/item/models/item.model';

//Itnerfaces para la consulta de la Matriz POA
export interface DtoMatrizPoaConsulta {
  // Jerarquía de la Actividad
  programa?: ProgramaModel;
  subprograma?: SubProgramaModel;
  proyecto?: ProyectoModel;
  actividad?: ActividadModel;

  // Jerarquía del Item
  presNaturaleza?: NaturalezaModel;
  presGrupo?: GrupoModel;
  presSubgrupo?: SubGrupoModel;
  item?: ItemModel;

  // Datos de la relación/intersección
  existeRelacion?: boolean;
  observacion?: string;
}


// Interfaces para la gestión del POA
export interface DtoPOA {
  id?: number;
  estado?: string;

  // Referencias a los IDs de Actividad e Item
  actividadID?: number; // igual que en Java
  itemID?: number;  

  // Auditoría
  usuarioCreacion?: string;
  fechaCreacion?: Date | string;
  usuarioModificacion?: string;
  fechaModificacion?: Date | string;
}

export interface DtoPOAUpdate {
  id?: number;
  estado?: string;
  actividadId?: number;
  itemId?: number;
}

export interface TablePageEvent {
    first?: number;
    rows?: number;
}
