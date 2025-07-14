// Menu de navegación para el POA
export const subMenuBuild_Form = [
    {
        label: 'Crear Formulario',
        icon: 'pi pi-fw pi-file', // Cambiado a un icono más genérico de formulario
        routerLink: ['/formBuild'],
    },
    {
        label: 'Configuración de Secciones',
        icon: 'pi pi-fw pi-window-maximize',
        routerLink: ['/formBuild/seccion'],
    },
    {
        label: 'Configuración de Campos',
        icon: 'pi pi-fw pi-sliders-h',
        routerLink: ['/formBuild/campo'],
    },
    {
        label: 'Formularios Maquetados',
        icon: 'pi pi-fw pi-table',
        routerLink: ['/formBuild/lista'],
    }
];