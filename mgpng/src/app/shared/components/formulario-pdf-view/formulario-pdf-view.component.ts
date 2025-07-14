import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FieldPreviewComponent } from '../field-preview/field-preview.component';
import { FieldPreviewService } from 'src/app/shared/components/field-preview/field-preview.service';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-formulario-pdf-view',
  standalone: true,
  templateUrl: './formulario-pdf-view.component.html',
  styleUrls: ['./formulario-pdf-view.component.scss'],
  imports: [CommonModule, FieldPreviewComponent, ButtonModule],
  providers: [DatePipe, FieldPreviewService]
})
export class FormularioPdfViewComponent {
  @Input() formulario: any;
  @Input() secciones: any[] = [];
  @Input() camposPorSeccion: any = {};
  @Input() today: any;
  @Output() deseleccionarFormulario = new EventEmitter<void>();

  // Paso actual del wizard
  currentStep: number = 0;

  constructor(private fieldPreviewService: FieldPreviewService) {}

  onDeseleccionarFormulario() {
    this.deseleccionarFormulario.emit();
  }

  // Navegación wizard
  goToNextStep() {
    if (this.currentStep < this.secciones.length - 1) {
      this.currentStep++;
    }
  }

  goToPreviousStep() {
    if (this.currentStep > 0) {
      this.currentStep--;
    }
  }

  isLastStep(): boolean {
    return this.currentStep === this.secciones.length - 1;
  }

  isFirstStep(): boolean {
    return this.currentStep === 0;
  }

  getOpciones(campo: any): string[] {
    return this.fieldPreviewService.getFieldOptions(campo);
  }

  getNumFilas(campo: any): number {
    if (campo.filas?.length && campo.columnas?.length) {
      return campo.filas.length;
    }
    if (campo.filas?.length) {
      return campo.filas.length;
    }
    return campo.maxFilas || campo.minFilas || 1;
  }

  getNumColumnas(campo: any): number {
    if (campo.filas?.length && campo.columnas?.length) {
      return campo.columnas.length;
    }
    if (campo.columnas?.length) {
      return campo.columnas.length;
    }
    return campo.maxColumnas || campo.minColumnas || 1;
  }

  getFila(campo: any, y: number): any {
    return campo.filas?.[y] || { tipo: 'input', y };
  }

  getColumna(campo: any, x: number): any {
    return campo.columnas?.[x] || { tipo: 'input', x };
  }

  getValorCelda(campo: any, x: number, y: number): any {
    return campo.celdas?.find((celda: any) => celda.x === x && celda.y === y)?.valor || '';
  }

  hasInputColumn(campo: any): boolean {
    return Array.isArray(campo.columnas) && campo.columnas.some((col: any) => col.tipo !== 'label');
  }

  allColumnsAreLabel(campo: any): boolean {
    return campo.columnas?.length && campo.columnas.every((col: any) => col.tipo === 'label');
  }

  allRowsAreLabel(campo: any): boolean {
    return campo.filas?.length && campo.filas.every((fila: any) => fila.tipo === 'label');
  }

  getColumnasTabla(campo: any): any[] {
    if (Array.isArray(campo?.columnas)) {
      return campo.columnas;
    }
    return [];
  }

  getFilasTabla(campo: any): any[] {
    if (Array.isArray(campo?.filas)) {
      return campo.filas;
    }
    return [];
  }
}
