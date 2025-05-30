import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { subMenuGestionPOA_ESTRATEGICO, subMenuGestionPOA_OPERATIVO } from '../componentes/gestion-poa/submenu';

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
                                items: subMenuGestionPOA_ESTRATEGICO,
                            },
                            {
                                label: 'OPERATIVO',
                                icon: 'pi pi-fw pi-cog',
                                items: subMenuGestionPOA_OPERATIVO,
                            }
                        ],
                    },
                    {
                        label: 'Planificación POA',
                        icon: 'pi pi-fw pi-check-square',
                        routerLink: ['/gestionPoa'],
                    },
                ],
            },
        ];
    }
}