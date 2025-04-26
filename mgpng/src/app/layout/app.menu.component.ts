import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { subMenuProyeccion } from '../componentes/proyeccion/submenu';


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
                        label: 'PRUEBA',
                        icon: 'pi pi-fw pi-home',
                        items: [
                            {
                                label: 'E-Commerce',
                                icon: 'pi pi-fw pi-home',
                                routerLink: ['prueba/prueba'],
                            },
                        ],
                    },
                    {
                        label: 'PRUEBA',
                        icon: 'pi pi-fw pi-calendar',
                        routerLink: ['prueba/prueba'],
                    },
                    {
                        label:'PROYECCIÓN',
                        icon: 'pi pi-fw pi-chart-line',
                        items: subMenuProyeccion,
                    }
                        
                ],
            },
        ];
    }
}
