import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { subMenuLineamiento_OPERATIVO } from '../componentes/lineamiento/operativo/submenu';
import { subMenuLineamiento_ESTRATEGICO } from '../componentes/lineamiento/estrategico/submenu';
import { subMenuGestionPOA_OPERATIVO } from '../componentes/gestion-poa/submenu';
import { subMenuBuild_Form } from '../componentes/form-build/submenu';


@Component({
    selector: 'app-menu',
    templateUrl: './app.menu.component.html',
})
export class AppMenuComponent implements OnInit {
    model: any[] = [];

    ngOnInit() {
        this.model = [
            {
                label: 'INICIO',
                icon: 'pi pi-home',
                items: [
                    {
                        label: 'LINEAMIENTOS',
                        icon: 'pi pi-fw pi-sitemap',
                        items: [
                            {
                                label: 'ESTRATÉGICO',
                                icon: 'pi pi-fw pi-briefcase',
                                items: subMenuLineamiento_ESTRATEGICO,
                            },
                            {
                                label: 'OPERATIVO',
                                icon: 'pi pi-fw pi-cog',
                                items: subMenuLineamiento_OPERATIVO,
                            }
                        ],
                    },
                    {
                        label: 'Planificación POA',
                        icon: 'pi pi-fw pi-check-square',
                        // Aquí solo va un elemento, no un array
                        items: subMenuGestionPOA_OPERATIVO
                    },
                    {
                        label: 'Formularios',
                        icon: 'pi pi-fw pi-file',
                        items: subMenuBuild_Form
                    },
                ],
            },
        ];
    }
}