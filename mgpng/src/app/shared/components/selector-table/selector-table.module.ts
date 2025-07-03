import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- Importa FormsModule
import { PaginatorModule } from 'primeng/paginator'; // <-- Importa PaginatorModule si usas PrimeNG
import { SelectorTableComponent } from './selector-table.component';

@NgModule({
  declarations: [
    SelectorTableComponent
  ],
  imports: [
    CommonModule,
    FormsModule,        // <-- Agrega aquí
    PaginatorModule     // <-- Agrega aquí
  ],
  exports: [SelectorTableComponent]
})
export class SelectorTableModule { }