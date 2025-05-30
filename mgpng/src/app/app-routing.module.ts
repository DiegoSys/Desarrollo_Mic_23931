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
                path: 'gestionPoa',
                data: { breadcrumb: 'Apps' },
                loadChildren: () =>
                    import('./componentes/gestion-poa/gestion-poa.module').then(
                        (m) => m.GestionPOAModule
                    ),
            }
        ],
    },
    { path: '**', redirectTo: '/notfound' },
];

@NgModule({
    imports: [RouterModule.forRoot(routes, routerOptions)],
    exports: [RouterModule],
})
export class AppRoutingModule {}
