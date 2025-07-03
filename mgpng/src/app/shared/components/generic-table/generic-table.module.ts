import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
import { FormsModule } from '@angular/forms'; // <-- Agrega esto
import { GenericTableComponent } from './generic-table.component';
import { CellFormatPipe } from './pipes/cell-format.pipe';


@NgModule({
  declarations: [
    GenericTableComponent,
    CellFormatPipe
  ],
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    RippleModule,
    FormsModule // <-- Y esto
  ],
  exports: [
    GenericTableComponent
  ]
})
export class GenericTableModule { }