import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DatePipe } from '@angular/common';
import { MensajeService } from 'src/app/config/utils/mensaje.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router'; // Importa RouterModule
import { GestionPOARoutingModule } from './gestion-poa-routing.module';



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


// Componente
import { GenericViewModule } from 'src/app/shared/components/generic-view/generic-view.module';
import { GenericFormModule } from 'src/app/shared/components/generic-form/generic-form.module';
import { GestionPOAFormComponent } from './form-gestion-poa/gestion-poa-form.component';
import { GestionPoaListComponent } from './gestion-poa-list.component';



import { GenericTableModule } from 'src/app/shared/components/generic-table/generic-table.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { DataTableComponent } from 'src/app/shared/components/data-table/data-table.component';
import { SummaryCardComponent } from 'src/app/shared/components/summary-card/summary-card.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

@NgModule({
  declarations: [
    GestionPOAFormComponent,
    GestionPoaListComponent
    
  ],
  imports: [
    CommonModule,
    SharedModule,
    DataTableComponent,
    CommonModule,
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
    SpinnerModule,
    InputTextareaModule, 
    GenericViewModule,
    GenericFormModule,
    MessagesModule,
    GestionPOARoutingModule,
    SummaryCardComponent
    
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
export class GestionPOAModule {}
