import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Table } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { MultiSelectModule } from 'primeng/multiselect';
import { DropdownModule } from 'primeng/dropdown';
import { TriStateCheckboxModule } from 'primeng/tristatecheckbox';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.scss'],
  standalone: true,
  imports: [
    TableModule,
    TagModule,
    InputTextModule,
    MultiSelectModule,
    DropdownModule,
    TriStateCheckboxModule,
    CommonModule
  ]
})
export class DataTableComponent implements OnChanges {
  @Input() data: any[] = [];
  @Input() columns: { field: string, header: string, filterType?: string }[] = [];
  @Input() loading: boolean = false;
  @Input() rows: number = 10;
  @Input() rowsPerPageOptions: number[] = [10, 25, 50];

  globalFilterFields: string[] = [];

  ngOnChanges(changes: SimpleChanges) {
    if (changes['columns'] && this.columns) {
      this.globalFilterFields = this.columns.map(col => col.field);
    }
  }

  filterGlobal(event: Event, dt: Table) {
    const input = event.target as HTMLInputElement;
    dt.filterGlobal(input.value, 'contains');
  }

  // Este método ya no es necesario si usas <p-columnFilter> en el template,
  // pero lo dejamos por si quieres filtros personalizados en el futuro.
  filterColumn(event: Event, dt: Table, field: string, mode: string) {
    const input = event.target as HTMLInputElement;
    dt.filter(input.value, field, mode);
  }

  // Función auxiliar para los inputs de filtro de columna
  filterInput(event: Event, filter: (value: string) => void) {
    const value = (event.target as HTMLInputElement).value;
    filter(value);
  }

  // Acceso seguro a campos anidados tipo 'programa.codigo'
  resolveFieldData(data: any, field: string): any {
    if (!data || !field) return '';
    // Evita mostrar el evento si por error llega aquí
    if (data && (data instanceof Event || Object.prototype.toString.call(data) === '[object InputEvent]')) return '';
    return field.split('.').reduce((obj, key) => (obj ? obj[key] : ''), data);
  }
}