import { RouterLink } from "@angular/router";

//Menu de navegación para el lineamiento estratégico
export const subMenuLineamiento_ESTRATEGICO = [
    {
        label: 'ODS',
        icon: 'pi pi-fw pi-globe',
        routerLink: ['/lineamiento/obj-des-sost'],
    },
    {
        //EJE (se refiere al EJE de los ODS)
        label: 'Eje de los ODS',
        //icono diferente referente a los Eje de los ODS
        icon: 'pi pi-fw pi-th-large',
        routerLink: ['/lineamiento/eje'],
    },
    {
        label: 'Plan Nacional',
        icon: 'pi pi-fw pi-book',
        routerLink: ['/lineamiento/plan-nacional'],
    },
    {
        label: 'Obj. Nacionales',
        //icono de objetivo nacional
        icon: 'pi pi-fw pi-flag',
        routerLink: ['/lineamiento/opn'],
    },
    {
        label: 'PDN',
        //icono de política de desarrollo nacional
        icon: 'pi pi-fw pi-shield',
        routerLink: ['/lineamiento/pdn'],
    },
    {
        label: 'METAS (del PND)',
        //icono de metas
        icon: 'pi pi-fw pi-star',
        routerLink: ['/lineamiento/meta'],
    },
    {
        label: 'Estrategias',
        //icono de estrategias
        icon: 'pi pi-fw pi-cog',
        routerLink: ['/lineamiento/estrategia'],
    },
    {
        label: 'Programa Nacional',
        //icono de programa nacional
        icon: 'pi pi-fw pi-briefcase',
        routerLink: ['/lineamiento/prog-nacional'],
    },
    {
        label: 'Obj. Estrategicos',
        icon: 'pi pi-fw pi-sitemap',
        //icono de objetivos estratégicos        
        routerLink: ['/lineamiento/obj-estra'],
    },
    {
        label: 'Obj. Operativos',
        //icono de objetivos operativos (no repetido)
                icon: 'pi pi-fw pi-check-circle',
        routerLink: ['/lineamiento/obj-oper'],
    },
    {
        label: 'Programa Inst.',//abrev
        //icono de programa institucional difernente
        icon: 'pi pi-fw pi-building',
        routerLink: ['/lineamiento/prog-inst'],
    },
    {
        label: 'Producto Inst.',
        //icono de producto institucional
        icon: 'pi pi-fw pi-box',
        routerLink: ['/lineamiento/prod-inst'],
    }
  
];

