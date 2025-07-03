import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';


const routes: Routes = [
    {
        path: 'clasif-presup',
        loadChildren: () =>
        import('./clasif-presup/clasif-presup.module').then(m => m.ClasifPresupModule)
    },
    {
        path: 'estruct-presup',
        loadChildren: () =>
        import('./estruct-presup/estruct-presup.module').then(m => m.EstructPresupModule)
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class OperativoRoutingModule {}
