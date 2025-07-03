import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

//Componentes del lineamiento estratégico
import { ObjDesSostListComponent } from './obj-des-sost/obj-des-sost-list.component';
import { ObjDesSostFormComponent } from './obj-des-sost/form-ods/obj-des-sost-form.component';
import { ObjDesSostDetailComponent } from './obj-des-sost/view-ods/obj-des-sost-detail.component';
import { PnListComponent } from './plan-nacional/pn-list.component';
import { PnFormComponent } from './plan-nacional/form-pn/pn-form.component';
import { PnDetailComponent } from './plan-nacional/view-pn/pn-detail.component';
import { EjeListComponent } from './eje/eje-list.component';
import { EjeFormComponent } from './eje/form-eje/eje-form.component';
import { EjeDetailComponent } from './eje/view-eje/eje-detail.component';
import { OpnListComponent } from './opn/opn-list.component';
import { OpnDetailComponent } from './opn/view-opn/opn-detail.component';
import { OpnFormComponent } from './opn/form-opn/opn-form.component';
import { PdnDetailComponent } from './pdn/view-pdn/pdn-detail.component';
import { PdnFormComponent } from './/pdn/form-pdn/pdn-form.component';
import { PdnListComponent } from './pdn/pdn-list.component';
import { MetaDetailComponent } from './meta/view-meta/meta-detail.component';
import { MetaFormComponent } from './meta/form-meta/meta-form.component';
import { MetaListComponent } from './meta/meta-list.component';
import { EstrategiaDetailComponent } from './estrategia/view-estrategia/estrategia-detail.component';
import { EstrategiaFormComponent } from './estrategia/form-estrategia/estrategia-form.component';
import { EstrategiaListComponent } from './estrategia/estrategia-list.component';
import { PnlDetailComponent } from './prog-nacional/view-pnl/pnl-detail.component';
import { PnlFormComponent } from './prog-nacional/form-pnl/pnl-form.component';
import { PnlListComponent } from './prog-nacional/pnl-list.component';
import { ObEsDetailComponent } from './obj-estrategico/view-ob-es/ob-es-detail.component';
import { ObEsFormComponent } from './obj-estrategico/form-ob-es/ob-es-form.component';
import { ObEsListComponent } from './obj-estrategico/ob-es-list.component';
import { ProgInsListComponent } from './prog-institucional/prog-ins-list.component';
import { ProgInsFormComponent } from './prog-institucional/form-prog-ins/prog-ins-form.component';
import { ProgInsDetailComponent } from './prog-institucional/view-prog-ins/prog-ins-detail.component';
import { ObjOperDetailComponent } from './obj-operativo/view-obj-oper/obj-oper-detail.component';
import { ObjOperFormComponent } from './obj-operativo/form-obj-oper/obj-oper-form.component';
import { ObjOperListComponent } from './obj-operativo/obj-oper.component';
import { ProdInstDetailComponent } from './prod-institucional/view-prod-inst/prod-inst-detail.component';
import { ProdInstFormComponent } from './prod-institucional/form-prod-inst/prod-inst-form.component';
import { ProdInstListComponent } from './prod-institucional/prod-inst-list.component';


const routes: Routes = [
    { 
        path: 'obj-des-sost', 
        component: ObjDesSostListComponent 
    },
    { 
        path: 'obj-des-sost/new', 
        component: ObjDesSostFormComponent
    },
    { 
        path: 'obj-des-sost/edit', 
        component: ObjDesSostFormComponent 
    },
    { 
        path: 'obj-des-sost/view', 
        component: ObjDesSostDetailComponent 
    },
    {
        path: 'plan-nacional',
        component: PnListComponent
    },
    {
        path: 'plan-nacional/new',
        component: PnFormComponent
    },
    {
        path: 'plan-nacional/edit',
        component: PnFormComponent
    },
    {
        path: 'plan-nacional/view',
        component: PnDetailComponent
    },
    {
        path: 'eje',
        component: EjeListComponent
    },
    {
        path: 'eje/new',
        component: EjeFormComponent
    },
    {
        path: 'eje/edit',
        component: EjeFormComponent
    },
    {
        path: 'eje/view',
        component: EjeDetailComponent
    },
    {
        path: 'opn',
        component: OpnListComponent
    },
    {
        path: 'opn/new',
        component: OpnFormComponent
    },
    {
        path: 'opn/edit',
        component: OpnFormComponent
    },
    {
        path: 'opn/view',
        component: OpnDetailComponent
    },
    {
        path: 'pdn',
        component: PdnListComponent
    },
    {
        path: 'pdn/new',
        component: PdnFormComponent
    },
    {
        path: 'pdn/edit',
        component: PdnFormComponent
    },
    {
        path: 'pdn/view',
        component: PdnDetailComponent
    },
    {
        path: 'meta',
        component: MetaListComponent
    },
    {
        path: 'meta/new',
        component: MetaFormComponent
    },
    {
        path: 'meta/edit',
        component: MetaFormComponent
    },
    {
        path: 'meta/view',
        component: MetaDetailComponent
    },
    {
        path: 'estrategia',
        component: EstrategiaListComponent
    },
    {
        path: 'estrategia/new',
        component: EstrategiaFormComponent
    },
    {
        path: 'estrategia/edit',
        component: EstrategiaFormComponent
    },
    {
        path: 'estrategia/view',
        component: EstrategiaDetailComponent
    },
    {
        path: 'prog-nacional',
        component: PnlListComponent
    },
    {
        path: 'prog-nacional/new',
        component: PnlFormComponent
    },
    {
        path: 'prog-nacional/edit',
        component: PnlFormComponent
    },
    {
        path: 'prog-nacional/view',
        component: PnlDetailComponent
    },
    {
        path: 'obj-estra',
        component: ObEsListComponent
    },
    {
        path: 'obj-estra/new',
        component: ObEsFormComponent
    },
    {
        path: 'obj-estra/edit',
        component: ObEsFormComponent
    },
    {
        path: 'obj-estra/view',
        component: ObEsDetailComponent
    },
    {
        path: 'prog-inst',
        component: ProgInsListComponent
    },
    {
        path: 'prog-inst/new',
        component: ProgInsFormComponent
    },
    {
        path: 'prog-inst/edit',
        component: ProgInsFormComponent
    },
    {
        path: 'prog-inst/view',
        component: ProgInsDetailComponent
    },
    {
        path: 'obj-oper',
        component: ObjOperListComponent
    },
    {
        path: 'obj-oper/new',
        component: ObjOperFormComponent
    },
    {
        path: 'obj-oper/edit',
        component: ObjOperFormComponent
    },
    {
        path: 'obj-oper/view',
        component: ObjOperDetailComponent
    },
    {
        path: 'prod-inst',
        component: ProdInstListComponent
    },
    {
        path: 'prod-inst/new',
        component: ProdInstFormComponent
    },
    {
        path: 'prod-inst/edit',
        component: ProdInstFormComponent
    },
    {
        path: 'prod-inst/view',
        component: ProdInstDetailComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class EstrategicoRoutingModule {}
