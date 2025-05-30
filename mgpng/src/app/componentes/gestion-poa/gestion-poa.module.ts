import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DatePipe } from '@angular/common';
import { GestionPOARoutingModule } from './gestion-poa-routing.module';
import { MensajeService } from 'src/app/config/utils/mensaje.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router'; // Importa RouterModule



// PrimeNG Modules
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { CardModule } from 'primeng/card';
import { StepsModule } from 'primeng/steps';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { MessagesModule } from 'primeng/messages';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { OdsService } from './obj-des-sost/ods.service';
import { TagModule } from 'primeng/tag'; // Importa el módulo de Tag
import { InputTextareaModule } from 'primeng/inputtextarea';

//importar el módulo spinner
import { SpinnerModule } from 'src/app/spinner/spinner.module';
import { SpinnerService } from 'src/app/spinner/spinner.service';


// Components
//IMPORTAR DE LINEAMIENTO ESTRATEGICO

import { ObjDesSostListComponent } from './obj-des-sost/obj-des-sost-list.component';
import { ObjDesSostFormComponent } from './obj-des-sost/form-ods/obj-des-sost-form.component';
import { ObjDesSostDetailComponent } from './obj-des-sost/view-ods/obj-des-sost-detail.component';
import { GestionPOAComponent } from './gestion-poa.component';
import { PnListComponent } from './plan-nacional/pn-list.component';
import { PnFormComponent } from './plan-nacional/form-pn/pn-form.component';
import { EjeListComponent } from './eje/eje-list.component';
import { EjeFormComponent } from './eje/form-eje/eje-form.component';
import { EjeDetailComponent } from './eje/view-eje/eje-detail.component';
import { OpnListComponent } from './opn/opn-list.component';
import { OpnDetailComponent } from './opn/view-opn/opn-detail.component';
import { OpnFormComponent } from './opn/form-opn/opn-form.component';
import { PdnDetailComponent } from './pdn/view-pdn/pdn-detail.component';
import { PdnFormComponent } from './pdn/form-pdn/pdn-form.component';
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
import { ObjOperListComponent } from './obj-operativo/obj-oper.component';
import { ObjOperFormComponent } from './obj-operativo/form-obj-oper/obj-oper-form.component';
import { ObjOperDetailComponent } from './obj-operativo/view-obj-oper/obj-oper-detail.component';
import { ProdInstDetailComponent } from './prod-institucional/view-prod-inst/prod-inst-detail.component';
import { ProdInstFormComponent } from './prod-institucional/form-prod-inst/prod-inst-form.component';
import { ProdInstListComponent } from './prod-institucional/prod-inst-list.component';

//IMPORTAR DE LINEAMIENTO OPERATIVO
import { NaturalezaDetailComponent } from './naturaleza/view-naturaleza/naturaleza-deatil.component';
import { NaturalezaFormComponent } from './naturaleza/form-naturaleza/naturaleza-form.component';
import { NaturalezaListComponent } from './naturaleza/naturaleza-list.component';
import { GrupoFormComponent } from './grupo/form-grupo/grupo-form.component';
import { GrupoListComponent } from './grupo/grupo-list.component';
import { GrupoDetailComponent } from './grupo/view-grupo/grupo-deatil.component';

//import { PnDetailComponent } from './plan-nacional/view-pn/pn-detail.component';
import { GenericViewModule } from 'src/app/shared/components/generic-view/generic-view.module';
import { GenericFormModule } from 'src/app/shared/components/generic-form/generic-form.module';
import { PnDetailComponent } from './plan-nacional/view-pn/pn-detail.component';


import { GenericTableModule } from 'src/app/shared/components/generic-table/generic-table.module';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
@NgModule({
  declarations: [
    // Componentes de Lineamiento Estratégico
    ObjDesSostListComponent,
    ObjDesSostFormComponent,
    ObjDesSostDetailComponent,
    GestionPOAComponent,
    PnListComponent,
    PnFormComponent,
    PnDetailComponent, // Asegúrate de que este componente esté declarado
    PnDetailComponent,
    EjeListComponent,
    EjeFormComponent,
    EjeDetailComponent,
    OpnListComponent,
    OpnDetailComponent,
    OpnFormComponent,
    PdnDetailComponent,
    PdnFormComponent,
    PdnListComponent,
    MetaDetailComponent,
    MetaFormComponent,
    MetaListComponent,
    EstrategiaDetailComponent,
    EstrategiaFormComponent,
    EstrategiaListComponent,
    PnlDetailComponent,
    PnlFormComponent,
    PnlListComponent,
    ObEsDetailComponent,
    ObEsFormComponent,
    ObEsListComponent,
    ProgInsListComponent,
    ProgInsFormComponent,
    ProgInsDetailComponent,
    ObjOperListComponent,
    ObjOperFormComponent,
    ObjOperDetailComponent,
    ProdInstDetailComponent,
    ProdInstFormComponent,
    ProdInstListComponent,
    // componentes de Lineamiento Operativo
    NaturalezaDetailComponent,
    NaturalezaFormComponent,
    NaturalezaListComponent,
    GrupoFormComponent,
    GrupoListComponent,
    GrupoDetailComponent

    
  ],
  imports: [
    GenericTableModule,
    CommonModule,
    GestionPOARoutingModule,
    FormsModule,
    ReactiveFormsModule,
    DropdownModule,
    ButtonModule,
    TooltipModule,
    CardModule,
    StepsModule,
    InputTextModule,
    TableModule,
    ToastModule,
    ConfirmDialogModule,
    RouterModule,
    TagModule, // <-- Añade este módulo
    TooltipModule, // <-- Añade si usas pTooltip
    SpinnerModule,
    InputTextareaModule, 
    GenericViewModule,
    GenericFormModule,
    MessagesModule
  ],
  providers: [
    MensajeService, 
    OdsService, 
    ConfirmationService,
    MessageService,
    SpinnerService,
    DatePipe
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GestionPOAModule {}
