import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { TableAction, TableConfig } from './interfaces/table-config.interface';
import { Subject } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-generic-table',
  templateUrl: './generic-table.component.html',
  styleUrls: ['./generic-table.component.scss']
})
export class GenericTableComponent implements OnInit {
  isSelected(row: any): boolean {
    return this.selectedRows?.includes(row);
  }

  private searchSubject = new Subject<{ [key: string]: string }>();
  private destroy$ = new Subject<void>();

  @Input() data: any[] = [];
  @Input() config: TableConfig;
  @Input() loading: boolean = false;
  @Input() totalRecords: number = 0;
  @Output() onRowSelect = new EventEmitter<any>();
  @Output() onPageChange = new EventEmitter<any>();
  @Output() onFilter = new EventEmitter<any>();
  @Output() onSearch = new EventEmitter<{ [key: string]: string }>(); // Nuevo Output
  @Output() onSortChange = new EventEmitter<{ sortField: string, sortOrder: string }>();


  selectedRows: any[] = [];
  first = 0;
  rows = 10;

  
  object = Object;

  // Campos de búsqueda locales
  search: { [key: string]: string } = {};


  ngOnInit() {
    this.data = this.data || [];
    this.config = this.config || {
      columns: [],
      actions: [],
      pagination: true,
      rowsPerPage: [10, 25, 50],
      selectionMode: 'single',
      rowTrackBy: ''
    };
    this.loading = this.loading || false;
    this.totalRecords = this.totalRecords || 0;

    // Debounce para búsqueda
    this.searchSubject.pipe(debounceTime(400), takeUntil(this.destroy$)).subscribe(criteria => {
      this.onSearch.emit(criteria);
    });
  }

  
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  handleRowSelect(event: any) {
    this.onRowSelect.emit(event);
  }

  handlePageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
    this.onPageChange.emit({
      first: this.first,
      rows: this.rows,
      page: Math.floor(this.first / this.rows),
      pageCount: Math.ceil(this.totalRecords / this.rows)
    });
  }

  handleSort(event: any) {
    this.onSortChange.emit({
      sortField: event.field,
      sortOrder: event.order === 1 ? 'asc' : 'desc'
    });
  }

  handleFilter(event: any) {
    this.onFilter.emit(event);
  }

  executeAction(action: TableAction, row: any) {
    if (!action.disabled || !action.disabled(row)) {
      action.handler(row);
    }
  }

  rowTrackBy(index: number, item: any): any {
    return this.config?.rowTrackBy ? item[this.config.rowTrackBy] : index;
  }

  // Nuevo método para emitir búsqueda con debounce
  emitSearch() {
    this.searchSubject.next({ ...this.search });
  }

  // Método para limpiar la búsqueda
  clearSearch() {
    this.search = {};
    this.emitSearch();
  }

}