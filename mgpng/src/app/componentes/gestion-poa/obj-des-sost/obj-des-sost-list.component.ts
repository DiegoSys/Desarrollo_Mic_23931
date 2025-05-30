import { Component, OnInit } from '@angular/core';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { TableLazyLoadEvent } from 'primeng/table';
import { Router } from '@angular/router';
import { OdsService } from './ods.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ActivatedRoute } from '@angular/router';
import { OdsModel, TablePageEvent } from './models/obj-des-sost.model';

@Component({
  selector: 'app-obj-des-sost-list',
  templateUrl: './obj-des-sost-list.component.html',
  styleUrls: ['./obj-des-sost-list.component.scss'],
  providers: [ConfirmationService, MessageService] 
})
export class ObjDesSostListComponent implements OnInit {
  odsList: OdsModel[] | null = null;
  totalRecords = 0;
  loading = true;
  loadingTemplate = true;
  first = 0;
  rows = 10;

  tableConfig: TableConfig = {
    columns: [
      { 
        field: 'codigo', 
        header: 'Código', 
        type: 'text'
      },
      { 
        field: 'descripcion', 
        header: 'Descripción', 
        type: 'text'
      },
      {
        field: 'fechaCreacion',
        header: 'Fecha Creación',
        type: 'date',
      }
      /*{ 
        field: 'alineacion', 
        header: 'Alineación', 
        type: 'text'
      },
      { field: 'fechaCreacion', header: 'Fecha Creación', type: 'date', format: 'dd/MM/yyyy' },
      { 
        field: 'estado', 
        header: 'Estado', 
        type: 'text',
        formatter: (value: string) => value === 'A' ? 'ACTIVO' : 'INACTIVO'
      }*/
    ],
    actions: [
      {
        icon: 'pi pi-eye',
        label: '',// 'Ver',
        handler: (row) => this.viewOds(row.codigo)
      },
      {
        icon: 'pi pi-pencil', 
        label: '',// 'Editar',
        handler: (row) => this.editOds(row.codigo)
      },
      {
        icon: 'pi pi-trash',
        label: '',// 'Eliminar',
        handler: (row) => this.confirmDelete(row.codigo),
        color: 'p-button-danger'
      }
    ],
    pagination: true,
    rowsPerPage: [10, 25, 50],
    selectionMode: 'single',
    rowTrackBy: 'codigo',
    globalFilterFields: ['codigo', 'descripcion']
  } as TableConfig;

  constructor(
    private odsService: OdsService,
    private router: Router,
    private route: ActivatedRoute,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.loadOds({ first: this.first, rows: this.rows });
  }

  loadOds(event: TableLazyLoadEvent): void {
    this.loading = true;
    this.first = event.first || 0;
    this.rows = event.rows || 10;

    const params = {
      page: Math.floor(this.first / this.rows),
      size: this.rows
    };

    this.odsService.getAll(params).subscribe({
      next: (response: TablePageEvent) => {
        this.odsList = response.content;
        this.totalRecords = response.totalElements;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading ODS:', err);
        this.showError('No se pudieron cargar los datos. Intente nuevamente.');
        this.loading = false;
        this.odsList = [];
      }
    });
  }

  viewOds(codigo: string): void {
    if (!codigo) {
      console.error('Código inválido:', codigo);
      return;
    }
    console.log('View ODS:', codigo);
    this.router.navigate(['view'], { 
      relativeTo: this.route, 
      queryParams: { codigo } 
    });
  }

  editOds(codigo: string): void {
    if (!codigo) {
      console.error('Código inválido:', codigo);
      return;
    }
    // ruta debug
    this.router.navigate(['edit'], { 
      relativeTo: this.route, 
      queryParams: { codigo } 
    });

  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: '¿Está seguro de eliminar este registro?',
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteOds(codigo)
    });
  }

  deleteOds(codigo: string): void {
    this.odsService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Registro eliminado correctamente',
          life: 3000
        });
        this.loadOds({ first: this.first, rows: this.rows });
      },
      error: (err) => {
        console.error('Error al eliminar:', err);
        this.showError('Error al eliminar el registro. Intente nuevamente.');
      }
    });
  }

  private showError(message: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message,
      life: 5000
    });
  }
}
