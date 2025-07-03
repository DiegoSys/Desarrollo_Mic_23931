import { Component, OnInit } from '@angular/core';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { TableLazyLoadEvent } from 'primeng/table';
import { Router } from '@angular/router';
import { PnService } from './pn.service';
import { ConfirmationService, MessageService, FilterMetadata } from 'primeng/api';
import { ActivatedRoute } from '@angular/router';
import { PnModel, TablePageEvent } from './models/pn.model';
// Si tienes un SpinnerService, descomenta la siguiente línea
// import { SpinnerService } from 'src/app/spinner/spinner.service';

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
  first = 0;
  rows = 10;
  sortField: string;
  sortOrder: string;
  searchCriteria: { [key: string]: string } = {}; // Nuevo campo para filtros

  tableConfig: TableConfig = {
    columns: [
      { field: 'codigo', header: 'Código', sortable: true, type: 'text', sortField: 'codigo' },
      { field: 'codigoObjDessostFk', header: 'Código ODS', sortable: true, type: 'text', sortField: 'codigoObjDessostFk' },
      { field: 'descripcion', header: 'Descripción', sortable: true, type: 'text', sortField: 'descripcion' },
      { field: 'fechaInicio', header: 'Fecha Inicio', type: 'date', format: 'dd/MM/yyyy' },
      { field: 'fechaFin', header: 'Fecha Fin', type: 'date', format: 'dd/MM/yyyy' }
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
    private messageService: MessageService,
    // Si tienes un SpinnerService, descomenta la siguiente línea
    // public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.loading = true;
    this.loadPn({ first: this.first, rows: this.rows });
  }

  // ...existing code...
  
    onSearch(criteria: { [key: string]: string }): void {
      this.searchCriteria = criteria;
      this.loadPn({ first: 0, rows: 10 });
    }
  
    onSortChange(event: { sortField: string, sortOrder: string }) {
      this.sortField = event.sortField;
      this.sortOrder = event.sortOrder;
      this.loadPn({ first: 0, rows: 10 });
    }
  
    loadPn(event: TablePageEvent = { first: 0, rows: 10 }): void {
      this.loading = true;
      // Si usas SpinnerService, descomenta la siguiente línea
      // this.spinnerService.show();
      this.first = event.first || 0;
      this.rows = event.rows || 10;
  
      const params: any = {
        page: Math.floor(this.first / this.rows),
        size: this.rows,
        sort: this.sortField,
        direction: this.sortOrder
      };
  
      if (Object.keys(this.searchCriteria).length > 0) {
        params.searchCriteria = this.searchCriteria;
      }
  
      this.pnService.getAll(params).subscribe({
        next: (response: any) => {
          this.pnList = response.content || [];
          this.totalRecords = response.totalElements || 0;
          this.loading = false;
          // Si usas SpinnerService, descomenta la siguiente línea
          // this.spinnerService.hide();
        },
        error: (err) => {
          this.showError('No se pudieron cargar los datos. Intente nuevamente.');
          this.loading = false;
          this.pnList = [];
          // Si usas SpinnerService, descomenta la siguiente línea
          // this.spinnerService.hide();
        }
      });
    }
  
  // ...existing code...


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
    this.loading = true;
    // Si usas SpinnerService, descomenta la siguiente línea
    // this.spinnerService.show();
    this.pnService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Registro eliminado correctamente',
          life: 3000
        });
        this.loadPn({ first: this.first, rows: this.rows });
        // Si usas SpinnerService, descomenta la siguiente línea
        // this.spinnerService.hide();
      },
      error: (err) => {
        console.error('Error al eliminar:', err);
        this.showError('Error al eliminar el registro. Intente nuevamente.');
        this.loading = false;
        // Si usas SpinnerService, descomenta la siguiente línea
        // this.spinnerService.hide();
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