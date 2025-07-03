import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

//Componentes del lineamiento operativo
import { NaturalezaDetailComponent } from './naturaleza/view-naturaleza/naturaleza-deatil.component';
import { NaturalezaFormComponent } from './naturaleza/form-naturaleza/naturaleza-form.component';
import { NaturalezaListComponent } from './naturaleza/naturaleza-list.component';
import { GrupoFormComponent } from './grupo/form-grupo/grupo-form.component';
import { GrupoListComponent } from './grupo/grupo-list.component';
import { GrupoDetailComponent } from './grupo/view-grupo/grupo-deatil.component';
import { SubGrupoListComponent } from './subGrupo/subGrupo-list.component';
import { SubGrupoFormComponent } from './subGrupo/form-subGrupo/subGrupo-form.component';
import { SubGrupoDetailComponent } from './subGrupo/view-subGrupo/subGrupo-deatil.component';
import { ItemDetailComponent } from './item/view-item/item-deatil.component';
import { ItemFormComponent } from './item/form-item/item-form.component';   
import { ItemListComponent } from './item/item-list.component';

const routes: Routes = [
    {
        path: '',
        redirectTo: 'naturaleza',
        pathMatch: 'full'
    },
    {
        path: 'naturaleza',
        component: NaturalezaListComponent
    },
    {
        path: 'naturaleza/new',
        component: NaturalezaFormComponent
    },
    {
        path: 'naturaleza/edit',
        component: NaturalezaFormComponent
    },
    {
        path: 'naturaleza/view',
        component: NaturalezaDetailComponent
    },
    {
        path: 'grupo',
        component: GrupoListComponent
    },
    {
        path: 'grupo/new',
        component: GrupoFormComponent
    },
    {
        path: 'grupo/edit',
        component: GrupoFormComponent
    },
    {
        path: 'grupo/view',
        component: GrupoDetailComponent
    },
    {
        path: 'subGrupo',
        component: SubGrupoListComponent
    },
    {
        path: 'subGrupo/new',
        component: SubGrupoFormComponent
    },
    {
        path: 'subGrupo/edit',
        component: SubGrupoFormComponent
    },
    {
        path: 'subGrupo/view',
        component: SubGrupoDetailComponent
    },
    {
        path: 'item',
        component: ItemListComponent
    },
    {
        path: 'item/new',
        component: ItemFormComponent
    },
    {
        path: 'item/edit',
        component: ItemFormComponent
    },
    {
        path: 'item/view',
        component: ItemDetailComponent
    },    
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ClasifPresupRoutingModule {}
