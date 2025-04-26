import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PruebaComponent } from './prueba/prueba.component';
const routes: Routes = [
    {
        path: 'prueba',
        component: PruebaComponent,
        canActivate: [],
        data: {},
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class PruebaRoutingModule {}
