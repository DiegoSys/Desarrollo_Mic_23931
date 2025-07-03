import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GestionPOAFormComponent } from './form-gestion-poa/gestion-poa-form.component';
import { GestionPoaListComponent } from './gestion-poa-list.component';


const routes: Routes = [
    { 
        path: 'new', 
        component:  GestionPOAFormComponent,
    },
    {
        path: '',
        component: GestionPoaListComponent,
    }

];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class GestionPOARoutingModule {}
