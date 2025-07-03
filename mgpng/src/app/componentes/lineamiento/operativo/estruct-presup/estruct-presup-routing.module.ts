import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

//Componentes del lineamiento operativo
import { ProgramaDetailComponent } from './programa/view-programa/programa-deatil.component';
import { ProgramaFormComponent } from './programa/form-programa/programa-form.component';
import { ProgramaListComponent } from './programa/programa-list.component';
import { SubProgramaDetailComponent } from './subPrograma/view-programa/subPrograma-deatil.component';
import { SubProgramaFormComponent } from './subPrograma/form-subPrograma/subPrograma-form.component';
import { SubProgramaListComponent } from './subPrograma/subPrograma-list.component';
import { ProyectoDetailComponent } from './proyecto/view-proyecto/proyecto-deatil.component';
import { ProyectoFormComponent } from './proyecto/form-proyecto/proyecto-form.component';
import { ProyectoListComponent } from './proyecto/proyecto-list.component';
import { ActividadListComponent } from './actividad/actividad-list.component';
import { ActividadFormComponent } from './actividad/form-actividad/actividad-form.component';   
import { ActividadDetailComponent } from './actividad/view-actividad/actividad-deatil.component';

const routes: Routes = [
    {
        path: 'programa',
        component: ProgramaListComponent
    },
    {
        path: 'programa/new',
        component: ProgramaFormComponent
    },
    {
        path: 'programa/edit',
        component: ProgramaFormComponent
    },
    {
        path: 'programa/view',
        component: ProgramaDetailComponent
    },
    {
        path: 'subPrograma',
        component: SubProgramaListComponent
    },
    {
        path: 'subPrograma/new',
        component: SubProgramaFormComponent
    },
    {
        path: 'subPrograma/edit',
        component: SubProgramaFormComponent
    },
    {
        path: 'subPrograma/view',
        component: SubProgramaDetailComponent
    },
    {
        path: 'proyecto',
        component: ProyectoListComponent
    },
    {
        path: 'proyecto/new',
        component: ProyectoFormComponent
    },
    {
        path: 'proyecto/edit',
        component: ProyectoFormComponent
    },
    {
        path: 'proyecto/view',
        component: ProyectoDetailComponent
    },
    {
        path: 'actividad',
        component: ActividadListComponent
    },
    {
        path: 'actividad/new',
        component: ActividadFormComponent
    },
    {
        path: 'actividad/edit',
        component: ActividadFormComponent
    },
    {
        path: 'actividad/view',
        component: ActividadDetailComponent
    },
    
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class EstructPresupRoutingModule {}
