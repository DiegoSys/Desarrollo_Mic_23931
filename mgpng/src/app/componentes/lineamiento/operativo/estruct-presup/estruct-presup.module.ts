import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DatePipe } from '@angular/common';
import { OperativoRoutingModule } from '../operativo-routing.module';
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
import { TagModule } from 'primeng/tag'; // Importa el módulo de Tag
import { InputTextareaModule } from 'primeng/inputtextarea';

//importar el módulo spinner
import { SpinnerModule } from 'src/app/spinner/spinner.module';
import { SpinnerService } from 'src/app/spinner/spinner.service';


// Components
import { ProgramaDetailComponent } from './programa/view-programa/programa-deatil.component';
import { ProgramaFormComponent } from './programa/form-programa/programa-form.component';
import { ProgramaListComponent } from './programa/programa-list.component';
import { SubProgramaDetailComponent } from './subPrograma/view-programa/subPrograma-deatil.component';
import { SubProgramaFormComponent } from './subPrograma/form-subPrograma/subPrograma-form.component';
import { SubProgramaListComponent } from './subPrograma/subPrograma-list.component';
import { ProyectoDetailComponent } from './proyecto/view-proyecto/proyecto-deatil.component'; 
import { ProyectoFormComponent } from './proyecto/form-proyecto/proyecto-form.component';
import { ProyectoListComponent } from './proyecto/proyecto-list.component';
import { ActividadDetailComponent } from './actividad/view-actividad/actividad-deatil.component';
import { ActividadFormComponent } from './actividad/form-actividad/actividad-form.component';
import { ActividadListComponent } from './actividad/actividad-list.component';

//import { PnDetailComponent } from './plan-nacional/view-pn/pn-detail.component';
import { GenericViewModule } from 'src/app/shared/components/generic-view/generic-view.module';
import { GenericFormModule } from 'src/app/shared/components/generic-form/generic-form.module';


import { GenericTableModule } from 'src/app/shared/components/generic-table/generic-table.module';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { EstructPresupRoutingModule } from './estruct-presup-routing.module';

@NgModule({
  declarations: [
    // componentes de Lineamiento Operativo

    ProgramaDetailComponent,
    ProgramaFormComponent,
    ProgramaListComponent,
    SubProgramaDetailComponent,
    SubProgramaFormComponent,
    SubProgramaListComponent,
    ProyectoDetailComponent,
    ProyectoFormComponent,
    ProyectoListComponent,
    ActividadDetailComponent,
    ActividadFormComponent,
    ActividadListComponent,
    
  ],
  imports: [
    GenericTableModule,
    CommonModule,
    OperativoRoutingModule,
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
    MessagesModule,
    EstructPresupRoutingModule, // Importa el módulo de rutas específico
  ],
  providers: [
    MensajeService, 
    ConfirmationService,
    MessageService,
    SpinnerService,
    DatePipe
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class EstructPresupModule {}
