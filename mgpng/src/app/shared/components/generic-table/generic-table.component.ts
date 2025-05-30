import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { TableColumn, TableAction, TableConfig } from './interfaces/table-config.interface';

@Component({
  selector: 'app-generic-table',
  templateUrl: './generic-table.component.html',
  styleUrls: ['./generic-table.component.scss']
})
export class GenericTableComponent implements OnInit {
  isSelected(row: any): boolean {
    return this.selectedRows?.includes(row);
  }

  @Input() data: any[] = [];
  @Input() config: TableConfig;
  @Input() loading: boolean = false;
  @Input() totalRecords: number = 0;

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
  }
  
  @Output() onRowSelect = new EventEmitter<any>();
  @Output() onPageChange = new EventEmitter<any>();
  @Output() onFilter = new EventEmitter<any>();

  selectedRows: any[] = [];
  first = 0;
  rows = 10;

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
}
