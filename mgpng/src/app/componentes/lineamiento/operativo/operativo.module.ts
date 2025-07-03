import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DatePipe } from '@angular/common';
import { OperativoRoutingModule } from './operativo-routing.module';
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

//import { PnDetailComponent } from './plan-nacional/view-pn/pn-detail.component';
import { GenericViewModule } from 'src/app/shared/components/generic-view/generic-view.module';
import { GenericFormModule } from 'src/app/shared/components/generic-form/generic-form.module';


import { GenericTableModule } from 'src/app/shared/components/generic-table/generic-table.module';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
@NgModule({
  declarations: [
    // componentes de Lineamiento Operativo
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
    MessagesModule
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
export class OperativoModule {}
