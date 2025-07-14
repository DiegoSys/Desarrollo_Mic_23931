import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FieldPreviewService } from 'src/app/shared/components/field-preview/field-preview.service';
@Component({
  selector: 'app-preview-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './preview-card.component.html',
  styleUrls: ['./preview-card.component.scss'],
  providers: [FieldPreviewService]
})
export class PreviewCardComponent {
  @Input() size: 'small' | 'medium' = 'medium';
  @Input() title: string = '';
  @Input() code: string = '';
  @Input() status?: string;
  @Input() statusText: string = '';
  @Input() description?: string;
  @Input() date?: Date;
  @Input() showFooter: boolean = true;
  @Input() showActions: boolean = true;
  @Input() showEdit: boolean = true;
  @Input() showClone: boolean = false;
  @Input() showDelete: boolean = true;
  @Input() showView: boolean = true;

  // Eventos básicos
  @Output() onClick = new EventEmitter<void>();
  @Output() onEdit = new EventEmitter<MouseEvent>();
  @Output() onClone = new EventEmitter<MouseEvent>();
  @Output() onDelete = new EventEmitter<MouseEvent>();
  @Output() onView = new EventEmitter<MouseEvent>();
  @Output() onEmptyStateAction = new EventEmitter<void>();
  
  // Estado vacío
  @Input() showEmptyState: boolean = false;
  @Input() emptyStateTitle?: string;
  @Input() emptyStateMessage?: string;
  @Input() emptyStateActionText?: string;

  // Funcionalidades específicas de secciones
  @Input() showRemoveFromTypology: boolean = false;
  @Input() showAssociateToTypology: boolean = false;
  @Input() sectionOrder?: number;
  @Input() isDraggable: boolean = false;
  @Input() isInDualView: boolean = false;
  @Input() typologyCode?: string;
  @Input() isAvailable: boolean = false;
  @Input() isAssociated: boolean = false;

  // Eventos específicos de secciones
  @Output() onRemoveFromTypology = new EventEmitter<MouseEvent>();
  @Output() onAssociateToTypology = new EventEmitter<MouseEvent>();
  @Output() onDragStart = new EventEmitter<DragEvent>();
  @Output() onDragEnd = new EventEmitter<DragEvent>();
  @Output() onDrop = new EventEmitter<DragEvent>();

  // Funcionalidad de campos (para cuando se navega a campos de una sección)
  @Input() showGoToFields: boolean = false;
  @Output() onGoToFields = new EventEmitter<void>();

  // Información adicional para preview
  @Input() fieldCount?: number;
  @Input() subsectionCount?: number;
  @Input() previewType: 'form' | 'section' | 'field' = 'form';

  // Propiedades específicas para campos
  @Input() fieldType?: string;
  @Input() campo?: any; // Para pasar el objeto campo completo

  constructor(private fieldPreviewService: FieldPreviewService) {}

  // Métodos para manejo de eventos
  handleClick(event: MouseEvent) {
    if (!this.isDraggable || !event.defaultPrevented) {
      this.onClick.emit();
    }
  }

  handleKeydown(event: Event) {
    const keyboardEvent = event as KeyboardEvent;
    if (keyboardEvent.key === 'Enter') {
      this.onClick.emit();
    }
  }

  handleEdit(event: MouseEvent) {
    event.stopPropagation();
    this.onEdit.emit(event);
  }

  handleClone(event: MouseEvent) {
    event.stopPropagation();
    this.onClone.emit(event);
  }

  handleDelete(event: MouseEvent) {
    event.stopPropagation();
    this.onDelete.emit(event);
  }

  handleView(event: MouseEvent) {
    event.stopPropagation();
    this.onView.emit(event);
  }

  handleRemoveFromTypology(event: MouseEvent) {
    event.stopPropagation();
    this.onRemoveFromTypology.emit(event);
  }

  handleAssociateToTypology(event: MouseEvent) {
    event.stopPropagation();
    this.onAssociateToTypology.emit(event);
  }

  handleGoToFields(event: MouseEvent) {
    event.stopPropagation();
    this.onGoToFields.emit();
  }

  // Métodos del FieldPreviewService
  getFieldIcon(tipoCampo?: string): string {
    return this.fieldPreviewService.getFieldIcon(tipoCampo);
  }

  getFieldTypeName(tipoCampo?: string): string {
    return this.fieldPreviewService.getFieldTypeName(tipoCampo);
  }

  getFieldOptions(campo: any): string[] {
    return this.fieldPreviewService.getFieldOptions(campo);
  }

  getTableTypeText(tipoConfiguracion: string): string {
    switch (tipoConfiguracion) {
      case 'SOLO_COLUMNAS':
        return 'Solo Columnas';
      case 'SOLO_FILAS':
        return 'Solo Filas';
      case 'COLUMNAS_Y_FILAS':
        return 'Columnas y Filas';
      case 'TABLA_PERSONALIZADA':
        return 'Tabla Personalizada';
      default:
        return 'Tabla';
    }
  }
  
  
  getColspan(headerLength: number): number {
    const maxCols = Math.min(headerLength, 3);
    return headerLength > 3 ? maxCols + 1 : maxCols;
  }

  // Verifica si el campo tiene propiedades booleanas activas
  hasFieldProperties(): boolean {
    if (!this.campo) return false;
    return this.campo.requerido === true || 
           this.campo.soloLectura === true || 
           this.campo.esMultiple === true;
  }

  // Getters para facilitar el uso en el template
  get showSectionActions(): boolean {
    return this.isInDualView && (this.showRemoveFromTypology || this.showAssociateToTypology);
  }

  get shouldShowOrder(): boolean {
    return this.isAssociated && this.sectionOrder !== undefined;
  }

  get cardClasses(): string {
    let classes = 'preview-card';
    
    if (this.size) {
      classes += ` card-${this.size}`;
    }
    
    if (this.isDraggable) {
      classes += ' draggable-card';
    }
    
    if (this.isInDualView) {
      classes += ' dual-view-card';
    }
    
    if (this.isAvailable) {
      classes += ' available-card';
    }
    
    if (this.isAssociated) {
      classes += ' associated-card';
    }
    
    return classes;
  }
}