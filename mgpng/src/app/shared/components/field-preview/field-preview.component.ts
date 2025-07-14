import { Component, Input, OnInit, OnChanges, SimpleChanges, DoCheck } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Campo } from '../../../componentes/form-build/models/form-build.model';
import { FieldPreviewService } from 'src/app/shared/components/field-preview/field-preview.service';
interface TableHeader {
  label: string;
}

interface TableRow {
  label: string;
  cells: TableCell[];
}

interface TableCell {
  type: 'input' | 'label' | 'header';
  value?: string;
  class?: string;
  isEditable?: boolean;
}

interface TableData {
  headers: TableHeader[];
  rows: TableRow[];
  columnCount: number;
  rowCount: number;
  info: string;
}

interface CustomTableData {
  rows: CustomTableRow[];
  columnCount: number;
  rowCount: number;
  info: string;
}

interface CustomTableRow {
  cells: CustomTableCell[];
}

interface CustomTableCell {
  type: 'input' | 'label' | 'header';
  value?: string;
  class?: string;
  isEditable?: boolean;
  fila: number;
  columna: number;
  colspan?: number;
}

@Component({
  selector: 'app-field-preview',
  standalone: true,
  imports: [CommonModule],
  providers: [FieldPreviewService],
  templateUrl: './field-preview.component.html',
  styleUrls: ['./field-preview.component.scss']
})
export class FieldPreviewComponent implements OnInit, OnChanges, DoCheck {
  @Input() campo!: Campo;
  @Input() showFieldInfo: boolean = true;
  @Input() showProperties: boolean = true;
  @Input() showLabel: boolean = true; // Nuevo parámetro para controlar la visibilidad del label
  @Input() isEditable: boolean = false; // Nuevo parámetro para habilitar/deshabilitar inputs

  // Computed table data for each configuration type
  soloColumnasTable: TableData | null = null;
  soloFilasTable: TableData | null = null;
  columnasYFilasTable: TableData | null = null;
  fallbackTable: TableData | null = null;
  customTable: CustomTableData | null = null;

  // Cache para detectar cambios en arrays
  private lastColumnasLength = 0;
  private lastFilasLength = 0;
  private lastConfigType = '';
  private lastCustomTableStructure: string = '';

  constructor(private fieldPreviewService: FieldPreviewService) {}

  ngOnInit() {
    this.computeTableData();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['campo']) {
      this.computeTableData();
    }
  }

  ngDoCheck() {
    // Detectar cambios en arrays anidados de columnas y filas
    if (this.campo && this.campo.tipoCampo === 'TABLA') {
      const currentColumnasLength = this.campo.columnas?.length || 0;
      const currentFilasLength = this.campo.filas?.length || 0;
      const currentConfigType = this.campo.tipoConfiguracionTabla || '';
      const currentCustomTableStructure = JSON.stringify(this.campo.estructuraTablaPersonalizada || {});
      
      // Solo recalcular si hay cambios reales
      if (currentColumnasLength !== this.lastColumnasLength || 
          currentFilasLength !== this.lastFilasLength ||
          currentConfigType !== this.lastConfigType ||
          currentCustomTableStructure !== this.lastCustomTableStructure) {
        
        this.lastColumnasLength = currentColumnasLength;
        this.lastFilasLength = currentFilasLength;
        this.lastConfigType = currentConfigType;
        this.lastCustomTableStructure = currentCustomTableStructure;
        
        this.computeTableData();
      }
    }
  }

  // Método público para forzar la actualización de la tabla
  public refreshTableData(): void {
    this.computeTableData();
  }

  getFieldIcon(tipoCampo?: string): string {
    return this.fieldPreviewService.getFieldIcon(tipoCampo);
  }

  getFieldTypeName(tipoCampo?: string): string {
    return this.fieldPreviewService.getFieldTypeName(tipoCampo);
  }

  getFieldOptions(campo: Campo): string[] {
    return this.fieldPreviewService.getFieldOptions(campo);
  }

  private computeTableData(): void {
    // Limpiar todas las tablas primero
    this.soloColumnasTable = null;
    this.soloFilasTable = null;
    this.columnasYFilasTable = null;
    this.fallbackTable = null;
    this.customTable = null;
    
    if (!this.campo || this.campo.tipoCampo !== 'TABLA') {
      return;
    }

    // Actualizar cache
    this.lastColumnasLength = this.campo.columnas?.length || 0;
    this.lastFilasLength = this.campo.filas?.length || 0;
    this.lastConfigType = this.campo.tipoConfiguracionTabla || '';
    this.lastCustomTableStructure = JSON.stringify(this.campo.estructuraTablaPersonalizada || {});

    const configType = this.campo.tipoConfiguracionTabla;
    
    if (configType === 'TABLA_PERSONALIZADA') {
      this.customTable = this.buildCustomTable();
    } else if (configType === 'SOLO_COLUMNAS') {
      this.soloColumnasTable = this.buildSoloColumnasTable();
    } else if (configType === 'SOLO_FILAS') {
      this.soloFilasTable = this.buildSoloFilasTable();
    } else if (configType === 'COLUMNAS_Y_FILAS') {
      this.columnasYFilasTable = this.buildColumnasYFilasTable();
    } else {
      this.fallbackTable = this.buildFallbackTable();
    }
  }

  private buildCustomTable(): CustomTableData {
    const estructura = this.campo.estructuraTablaPersonalizada;
    
    if (!estructura || !estructura.filas) {
      return this.createDefaultCustomTable();
    }

    // Formato de filas como array de objetos
    if (Array.isArray(estructura.filas)) {
      return this.buildCustomTableFromArray(estructura.filas as any[]);
    }

    // Formato original con filas/columnas como números
    return this.buildCustomTableFromNumbers(estructura);
  }

  private createDefaultCustomTable(): CustomTableData {
    return {
      rows: [{
        cells: [this.createDefaultCell(0, 0)]
      }],
      columnCount: 1,
      rowCount: 1,
      info: 'Tabla personalizada (estructura no definida)'
    };
  }

  private createDefaultCell(fila: number, columna: number): CustomTableCell {
    return {
      type: 'input',
      value: '',
      class: '',
      isEditable: true,
      fila,
      columna,
      colspan: 1
    };
  }

  private createCustomCell(celdaData: any, fila: number, columna: number): CustomTableCell {
    return {
      type: celdaData.tipo as 'input' | 'label' | 'header',
      value: celdaData.contenido || celdaData.valor || '',
      class: celdaData.clase || '',
      isEditable: celdaData.esEditable !== false,
      fila,
      columna,
      colspan: 1
    };
  }

  private createEmptyCell(fila: number, columna: number): CustomTableCell {
    return {
      type: 'input',
      value: '',
      class: 'empty-cell',
      isEditable: false,
      fila,
      columna,
      colspan: 1
    };
  }

  private buildCustomTableFromArray(filasArray: any[]): CustomTableData {
    const previewFilas = filasArray.length;
    const maxColumnas = Math.max(...filasArray.map(fila => fila.celdas?.length || 0));
    
    const rows: CustomTableRow[] = [];
    for (let fila = 0; fila < previewFilas; fila++) {
      const filaData = filasArray[fila];
      const cells: CustomTableCell[] = [];
      
      const cellCount = filaData.celdas?.length || 0;
      
      // Si la fila tiene menos celdas que el máximo, calcular colspan para distribuir el espacio
      for (let columna = 0; columna < cellCount; columna++) {
        const celdaData = filaData.celdas[columna];
        let colspan = 1;
        
        // Si es la última celda de una fila que tiene menos columnas que el máximo,
        // expandirla para ocupar el espacio restante
        if (cellCount < maxColumnas && columna === cellCount - 1) {
          colspan = maxColumnas - cellCount + 1;
        }
        
        const cell = this.createCustomCell(celdaData, fila, columna);
        cell.colspan = colspan;
        cells.push(cell);
      }
      
      rows.push({ cells });
    }

    return {
      rows,
      columnCount: maxColumnas,
      rowCount: previewFilas,
      info: `Tabla personalizada adaptable (${maxColumnas} columnas × ${filasArray.length} filas)`
    };
  }

  private buildCustomTableFromNumbers(estructura: any): CustomTableData {
    const previewFilas = estructura.filas as number;
    const previewColumnas = estructura.columnas as number;
    
    const rows: CustomTableRow[] = [];
    for (let fila = 0; fila < previewFilas; fila++) {
      const cells: CustomTableCell[] = [];
      for (let columna = 0; columna < previewColumnas; columna++) {
        const celdaPersonalizada = estructura.celdas?.find((c: any) => c.fila === fila && c.columna === columna);
        cells.push(celdaPersonalizada
          ? this.createCustomCell(celdaPersonalizada, fila, columna)
          : this.createDefaultCell(fila, columna));
      }
      rows.push({ cells });
    }

    return {
      rows,
      columnCount: previewColumnas,
      rowCount: previewFilas,
      info: `Tabla personalizada ${estructura.columnas} × ${estructura.filas}`
    };
  }

  private buildSoloColumnasTable(): TableData {
    const columnas = this.campo.columnas || [];
    
    const headers = columnas.map((col, index) => ({
      label: col.nombre || `Columna ${index + 1}`
    }));
    
    const rows = [{
      label: '',
      cells: headers.map(() => ({ type: 'input' as const }))
    }];

    return this.createTableData(headers, rows, columnas.length, 1, `${columnas.length} columnas`);
  }

  private buildSoloFilasTable(): TableData {
    const filas = this.campo.filas || [];
    
    const headers = [
      { label: 'Fila' },
      { label: 'Valor' }
    ];
    
    const rows = filas.map((fila, index) => ({
      label: fila.nombre || `Fila ${index + 1}`,
      cells: [{ type: 'input' as const }]
    }));

    return this.createTableData(headers, rows, 2, filas.length, `${filas.length} filas`);
  }

  private buildColumnasYFilasTable(): TableData {
    const columnas = this.campo.columnas || [];
    const filas = this.campo.filas || [];
    
    const headers = [
      { label: '' },
      ...columnas.map((col, index) => ({
        label: col.nombre || `Columna ${index + 1}`
      }))
    ];
    
    const rows = filas.map((fila, rowIndex) => ({
      label: fila.nombre || `Fila ${rowIndex + 1}`,
      cells: columnas.map(() => ({ type: 'input' as const }))
    }));

    return this.createTableData(headers, rows, columnas.length, filas.length, `${columnas.length} columnas × ${filas.length} filas`);
  }

  private createTableData(headers: TableHeader[], rows: TableRow[], columnCount: number, rowCount: number, info: string): TableData {
    return {
      headers,
      rows,
      columnCount,
      rowCount,
      info
    };
  }

  private buildFallbackTable(): TableData {
    const columnas = this.campo.columnas || [];
    const filas = this.campo.filas || [];
    
    if (filas.length && !columnas.length) {
      return this.buildFilasOnlyFallback(filas);
    } else if (columnas.length && !filas.length) {
      return this.buildColumnasOnlyFallback(columnas);
    }

    // Default fallback
    return this.createTableData(
      [{ label: 'Columna 1' }],
      [{
        label: '',
        cells: [{ type: 'input' as const }]
      }],
      1,
      1,
      '1 columnas × 1 filas'
    );
  }

  private buildFilasOnlyFallback(filas: any[]): TableData {
    const headers = [{ label: '' }];
    const rows = filas.map((fila, index) => ({
      label: fila.nombre || `Fila ${index + 1}`,
      cells: [{ type: 'input' as const }]
    }));
    
    return this.createTableData(headers, rows, 1, filas.length, `1 columnas × ${filas.length} filas`);
  }

  private buildColumnasOnlyFallback(columnas: any[]): TableData {
    const headers = columnas.map((col, index) => ({
      label: col.nombre || `Columna ${index + 1}`
    }));
    
    const cells = columnas.map(() => ({ type: 'input' as const }));
    const rows = [{
      label: '',
      cells: cells
    }];

    return this.createTableData(headers, rows, columnas.length, 1, `${columnas.length} columnas × 1 filas`);
  }

  // Verifica si el campo tiene propiedades booleanas activas
  hasFieldProperties(): boolean {
    if (!this.campo) return false;
    return this.campo.requerido === true || 
           this.campo.soloLectura === true || 
           this.campo.esMultiple === true;
  }

  isKnownFieldType(tipoCampo: string): boolean {
    const knownTypes = [
      'TXT', 'AREA', 'NUM', 'SEL', 'SELM', 'TABLA', 'CHK', 'RADIO', 'DATE', 'FILE'
    ];
    return knownTypes.includes(tipoCampo);
  }
}