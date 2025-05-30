import { RouterLink } from "@angular/router";

//Menu de navegación para el lineamiento estratégico
export const subMenuGestionPOA_ESTRATEGICO = [
    {
        label: 'ODS',
        icon: 'pi pi-fw pi-globe',
        routerLink: ['/gestionPoa/obj-des-sost'],
    },
    {
        label: 'Planes Nacionales',
        icon: 'pi pi-fw pi-book',
        routerLink: ['/gestionPoa/plan-nacional'],
    },
    {
        //EJE (se refiere al EJE de los ODS)
        label: 'Eje de los ODS',
        //icono diferente referente a los Eje de los ODS
        icon: 'pi pi-fw pi-th-large',
        routerLink: ['/gestionPoa/eje'],
    },
    {
        label: 'OPN',
        //icono de objetivo nacional
        icon: 'pi pi-fw pi-flag',
        routerLink: ['/gestionPoa/opn'],
    },
    {
        label: 'PDN',
        //icono de política de desarrollo nacional
        icon: 'pi pi-fw pi-shield',
        routerLink: ['/gestionPoa/pdn'],
    },
    {
        label: 'METAS (del PND)',
        //icono de metas
        icon: 'pi pi-fw pi-star',
        routerLink: ['/gestionPoa/meta'],
    },
    {
        label: 'Estrategias',
        //icono de estrategias
        icon: 'pi pi-fw pi-cog',
        routerLink: ['/gestionPoa/estrategia'],
    },
    {
        label: 'Programa Nacional',
        //icono de programa nacional
        icon: 'pi pi-fw pi-briefcase',
        routerLink: ['/gestionPoa/prog-nacional'],
    },
    {
        label: 'Obj. Estrategicos',
        icon: 'pi pi-fw pi-sitemap',
        //icono de objetivos estratégicos        
        routerLink: ['/gestionPoa/obj-estra'],
    },
    {
        label: 'Programa Inst.',//abrev
        //icono de programa institucional difernente
        icon: 'pi pi-fw pi-building',
        routerLink: ['/gestionPoa/prog-inst'],
    },
    {
        label: 'Objetivos Operativos',
        //icono de objetivos operativos (no repetido)
                icon: 'pi pi-fw pi-check-circle',
        routerLink: ['/gestionPoa/obj-oper'],
    },
    {
        label: 'Producto Inst.',
        //icono de producto institucional
        icon: 'pi pi-fw pi-box',
        routerLink: ['/gestionPoa/prod-inst'],
    }
  
];

//Menú de navegación para el lineamiento operativo
export const subMenuGestionPOA_OPERATIVO = [
    {
        label: 'Naturaleza',
        icon: 'pi pi-fw pi-slack',
        routerLink: ['/gestionPoa/naturaleza'],
    },
    {
        label: 'Grupo',
        icon: 'pi pi-fw pi-users',
        routerLink: ['/gestionPoa/grupo'],
    },
    {
        label: 'Sub Grupo',
        icon: 'pi pi-fw pi-user',
        routerLink: ['/gestionPoa/sub-grupo'],
    },
    {
        label: 'Item',
        icon: 'pi pi-fw pi-file',
        routerLink: ['/gestionPoa/item'],
    }
    
];

// menú completo para otros usos:
export const subMenuGestionPOA = [
    ...subMenuGestionPOA_ESTRATEGICO,
    ...subMenuGestionPOA_OPERATIVO
];
