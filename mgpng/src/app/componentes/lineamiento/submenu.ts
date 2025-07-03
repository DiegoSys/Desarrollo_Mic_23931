import { subMenuLineamiento_OPERATIVO } from "./operativo/submenu";
import { subMenuLineamiento_ESTRATEGICO } from "./estrategico/submenu";
// menú completo para otros usos:
export const subMenuLineamiento = [
    ...subMenuLineamiento_ESTRATEGICO,
    ...subMenuLineamiento_OPERATIVO
];
