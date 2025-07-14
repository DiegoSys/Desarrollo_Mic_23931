import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
//import { GestionPOAFormComponent } from './form-gestion-poa/gestion-poa-form.component';
import { FormBuildListComponent } from './form-build-list.component';
import { SeccionListComponent } from './seccion/seccion-list.component';
import { CampoListComponent } from './campo/campo-list.component';
import { BuilderViewComponent } from './builder-view/builder-view.component';
const routes: Routes = [
    {
        path: '',
        component: FormBuildListComponent,
    },
    {
        path: 'seccion',
        component: SeccionListComponent,
    },
    {
        path: 'campo',
        component: CampoListComponent,
    },
    {
        path: 'lista',
        component: BuilderViewComponent,
    }

];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class FormBuildRoutingModule {}
