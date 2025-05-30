export interface TableColumn {
  field: string;
  header: string;
  type?: 'text'|'number'|'date'|'boolean'|'custom';
  format?: string;
  width?: string;
  hidden?: boolean;

}

export interface TableAction {
  icon: string;
  label: string;
  handler: (row: any) => void;
  color?: string;
  disabled?: (row: any) => boolean;
  visible?: (row: any) => boolean;
}

export interface TableConfig {
  columns: TableColumn[];
  actions?: TableAction[];
  pagination?: boolean;
  rowsPerPage?: number[];
  globalFilterFields?: string[];
  selectionMode?: 'single'|'multiple';
  rowTrackBy?: string;
  showCurrentPageReport?: boolean;
  responsiveLayout?: 'scroll'|'stack';
  
}
