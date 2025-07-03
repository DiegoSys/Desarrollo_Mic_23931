import { subMenuLineamiento_OPERATIVO_CLASIF_PRESUP } from "./clasif-presup/submenu";
import { subMenuLineamiento_OPERATIVO_ESTRUCT_PRESUP } from "./estruct-presup/submenu";

//Menú de navegación para el lineamiento operativo
export const subMenuLineamiento_OPERATIVO = [
    ...subMenuLineamiento_OPERATIVO_CLASIF_PRESUP,
    ...subMenuLineamiento_OPERATIVO_ESTRUCT_PRESUP,
];
