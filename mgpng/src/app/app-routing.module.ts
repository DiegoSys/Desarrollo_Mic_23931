import { NgModule } from '@angular/core';
import { ExtraOptions, RouterModule, Routes } from '@angular/router';
import { AppLayoutComponent } from './layout/app.layout.component';

const routerOptions: ExtraOptions = {
    anchorScrolling: 'enabled',
};

const routes: Routes = [
    {
        path: '',
        component: AppLayoutComponent,
        children: [
            {
                path: 'profile',
                data: { breadcrumb: 'profile' },
                loadChildren: () =>
                    import('./componentes/profile/profile.module').then(
                        (m) => m.ProfileModule
                    ),
            },
            {
                path: 'prueba',
                data: { breadcrumb: 'Apps' },
                loadChildren: () =>
                    import('./componentes/prueba/prueba.module').then(
                        (m) => m.PruebaModule
                    ),
            },
            {
                path: 'proyeccion',
                data: { breadcrumb: 'Apps' },
                loadChildren: () =>
                    import('./componentes/proyeccion/proyeccion.module').then(
                        (m) => m.ProyeccionModule
                    ),
            }
        ],
    },
    // { path: 'landing', loadChildren: () => import('./demo/components/landing/landing.module').then(m => m.LandingModule) },
    { path: '**', redirectTo: '/notfound' },
];

@NgModule({
    imports: [RouterModule.forRoot(routes, routerOptions)],
    exports: [RouterModule],
})
export class AppRoutingModule {}
