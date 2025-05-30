import { Component, OnInit } from '@angular/core';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { TableLazyLoadEvent } from 'primeng/table';
import { Router } from '@angular/router';
import { PnService } from './pn.service';
import { ConfirmationService, MessageService, FilterMetadata } from 'primeng/api';
import { ActivatedRoute } from '@angular/router';
import { PnModel, TablePageEvent } from './models/pn.model';

@Component({
  selector: 'app-pn-list',
  templateUrl: './pn-list.component.html',
  styleUrls: ['./pn-list.component.scss'],
  providers: [ConfirmationService, MessageService] 
})
export class PnListComponent implements OnInit {
  pnList: PnModel[] | null = null;
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
        sortable: true, 
        type: 'text',
        sortField: 'codigo'
      },
      {
        field: 'codigoObjDessostFk', 
        header: 'Código ODS', 
        sortable: true, 
        type: 'text',
        sortField: 'codigoObjDessostFk'
      },
      { 
        field: 'descripcion', 
        header: 'Descripción', 
        sortable: true, 
        type: 'text',
        sortField: 'descripcion'
      },
      { 
        field: 'fechaInicio', 
        header: 'Fecha Inicio', 
        type: 'date', 
        format: 'dd/MM/yyyy' 
      },
      { 
        field: 'fechaFin', 
        header: 'Fecha Fin', 
        type: 'date', 
        format: 'dd/MM/yyyy' 
      }
    ],
    actions: [
      {
        icon: 'pi pi-eye',
        label: '',
        handler: (row) => this.viewPn(row.codigo)
      },
      {
        icon: 'pi pi-pencil', 
        label: '',
        handler: (row) => this.editPn(row.codigo)
      },
      {
        icon: 'pi pi-trash',
        label: '',
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
    private pnService: PnService,
    private router: Router,
    private route: ActivatedRoute,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.loadPn({ first: this.first, rows: this.rows });
  }

  loadPn(event: TableLazyLoadEvent): void {
    this.loading = true;
    this.first = event.first || 0;
    this.rows = event.rows || 10;

    const params: any = {
      page: Math.floor(this.first / this.rows),
      size: this.rows,
      totalRecords: this.totalRecords
    };

    if (event.sortField) {
      params.sort = event.sortField;
      params.order = event.sortOrder === 1 ? 'asc' : 'desc';
    }

    this.pnService.getAll(params).subscribe({
      next: (response: any) => {
        
        // Handle both array and paginated response formats
        let data = Array.isArray(response) ? response : 
                  response.content ? response.content : 
                  response.data ? response.data : 
                  response;

        // Apply client-side filtering if needed
        if (event.filters && typeof event.filters === 'object') {
          const globalFilter = (event.filters as {global?: FilterMetadata}).global;
          if (globalFilter?.value) {
            const searchTerm = globalFilter.value.toLowerCase();
            data = data.filter((item: PnModel) => 
              item.codigo?.toLowerCase().includes(searchTerm) || 
              item.descripcion?.toLowerCase().includes(searchTerm));
          }
        }

        this.pnList = data;
        this.totalRecords = response.totalElements || response.total || data.length;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading PN:', err);
        this.showError('No se pudieron cargar los datos. Intente nuevamente.');
        this.loading = false;
        this.pnList = [];
      }
    });
  }

  viewPn(codigo: string): void {
    if (!codigo) {
      console.error('Código inválido:', codigo);
      return;
    }
    this.router.navigate(['view'], { 
      relativeTo: this.route, 
      queryParams: { codigo } 
    });
  }

  editPn(codigo: string): void {
    if (!codigo) {
      console.error('Código inválido:', codigo);
      return;
    }
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
      accept: () => this.deletePn(codigo)
    });
  }

  deletePn(codigo: string): void {
    this.pnService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Registro eliminado correctamente',
          life: 3000
        });
        this.loadPn({ first: this.first, rows: this.rows });
      },
      error: (err) => {
        console.error('Error al eliminar:', err);
        this.showError('Error al eliminar el registro. Intente nuevamente.');
      }
    });
  }

  onSort(event: TablePageEvent) {
    this.loadPn({
      first: this.first,
      rows: this.rows,
      sortField: event.sortField,
      sortOrder: event.sortOrder === 'asc' ? 1 : -1
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
