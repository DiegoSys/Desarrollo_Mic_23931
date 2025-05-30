import { Component, Input, OnInit } from '@angular/core';

export interface ViewField {
  // Identificador único del campo
  key: string;
  // Etiqueta a mostrar
  label: string;
  // Valor del campo
  value: any;
  // Tipo de campo (texto, estado, etc)
  type?: 'text' | 'status' | 'multiline' | 'custom' | 'date' | 'number';
  // Configuración específica por tipo
  config?: {
    // Para tipo status
    status?: {
      activeValue: any;
      activeLabel?: string;
      inactiveValue: any;
      inactiveLabel?: string;
      colors?: {
        active?: string;
        inactive?: string;
      };
    };
    // Para tipo date
    dateFormat?: string;
    // Para tipo number
    numberFormat?: string;
  };
  // Ancho del campo
  width?: 'half' | 'full' | 'third' | 'quarter';
  // Icono opcional
  icon?: string;
  // Clases CSS adicionales
  cssClass?: string;
  // Orden de visualización
  order?: number;
}

@Component({
  selector: 'app-generic-view',
  templateUrl: './generic-view.component.html',
  styleUrls: ['./generic-view.component.scss']
})
export class GenericViewComponent implements OnInit {
  @Input() title: string = '';
  @Input() subtitle: string = '';
  @Input() icon: string = 'pi pi-eye';
  @Input() fields: ViewField[] = [];
  @Input() layout: 'grid' | 'list' = 'grid';
  @Input() gridColumns: number = 2;
  @Input() theme: 'default' | 'condensed' | 'card' = 'default';
  @Input() loading: boolean = false;
  @Input() backRoute: string = '';
  @Input() customActions: any[] = [];

  constructor() { }

  ngOnInit(): void {
    // Ordenar campos por orden si está definido
    if (this.fields.some(f => f.order !== undefined)) {
      this.fields.sort((a, b) => (a.order || 0) - (b.order || 0));
    }
  }
}
