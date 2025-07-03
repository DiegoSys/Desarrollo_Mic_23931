import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-selector-table',
  templateUrl: './selector-table.component.html',
  styleUrls: ['./selector-table.component.scss']
})
export class SelectorTableComponent implements OnChanges {
  @Input() items: any[] = [];
  @Input() selected: any[] = [];
  @Input() labelField: string = 'codigo';
  @Input() descField?: string;
  @Input() loading: boolean = false;
  @Input() totalRecords: number = 0;
  @Input() page: number = 0;
  @Input() size: number = 10;
  @Input() multiple: boolean = true;
  @Input() searchValue: string = '';
  @Output() searchValueChange = new EventEmitter<string>();

  @Output() selectionChange = new EventEmitter<any[]>();
  @Output() onPageChange = new EventEmitter<any>();
  @Output() onSearch = new EventEmitter<{ [key: string]: string }>();
  @Output() onSortChange = new EventEmitter<{ sortField: string, sortOrder: string | number }>();

  private searchSubject = new Subject<string>();

  constructor() {
    this.searchSubject.pipe(debounceTime(350)).subscribe(value => {
      this.handleSearch();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selected'] && !changes['selected'].firstChange) {
      this.selected = changes['selected'].currentValue || [];
    }
  }

  isSelected(item: any): boolean {
    return this.selected.some(sel => sel[this.labelField] === item[this.labelField]);
  }

  toggleSelection(item: any): void {
    let newSelected;
    if (this.multiple) {
      if (this.isSelected(item)) {
        newSelected = this.selected.filter(sel => sel[this.labelField] !== item[this.labelField]);
      } else {
        newSelected = [...this.selected, item];
      }
    } else {
      if (this.isSelected(item)) {
        newSelected = [];
      } else {
        newSelected = [item];
      }
    }
    this.selectionChange.emit(newSelected);
  }

  handlePageChange(event: any) {
    this.onPageChange.emit(event);
  }

  handleSortChange(field: string, order: string | number) {
    this.onSortChange.emit({ sortField: field, sortOrder: order });
  }

  handleSearch() {
    this.onSearch.emit({ [this.labelField]: this.searchValue });
  }

  onSearchInputChange(value: string) {
    this.searchValueChange.emit(value);
    this.searchSubject.next(value);
  }
}