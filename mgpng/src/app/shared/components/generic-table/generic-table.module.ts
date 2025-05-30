import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
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
    RippleModule
  ],
  exports: [
    GenericTableComponent
  ]
})
export class GenericTableModule { }
