import { Component, OnInit } from '@angular/core';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { TableLazyLoadEvent } from 'primeng/table';
import { Router } from '@angular/router';
import { OdsService } from './ods.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ActivatedRoute } from '@angular/router';
import { OdsModel, TablePageEvent } from './models/obj-des-sost.model';
import { SpinnerService } from 'src/app/spinner/spinner.service';

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
  first = 0;
  rows = 10;

  sortField: string;
  sortOrder: string;
  searchCriteria: { [key: string]: string } = {}; // Nuevo campo para filtros

  tableConfig: TableConfig = {
    columns: [
      { field: 'codigo', header: 'Código', type: 'text' },
      { field: 'descripcion', header: 'Descripción', type: 'text' },
      {
        field: 'fechaCreacion',
        header: 'Fecha Creación',
        type: 'date',
      }
    ],
    actions: [
      {
        icon: 'pi pi-eye',
        label: '',
        handler: (row) => this.viewOds(row.codigo)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (row) => this.editOds(row.codigo)
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
    private odsService: OdsService,
    private router: Router,
    private route: ActivatedRoute,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    // Solo inicializa loading, NO llames a loadOds aquí si la tabla lo hace automáticamente
    this.loading = true;
  }

  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadOds({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadOds({ first: 0, rows: 10 });
  }

  loadOds(event: TableLazyLoadEvent): void {
    this.loading = true;
    this.spinnerService.show();
    this.first = event.first || 0;
    this.rows = event.rows || 10;

    const params: any = {
      page: Math.floor(this.first / this.rows),
      size: this.rows,
      sort: this.sortField,
      direction: this.sortOrder,
    };

    if (Object.keys(this.searchCriteria).length > 0) {
      params.searchCriteria = this.searchCriteria;
    }

    this.odsService.getAll(params).subscribe({
      next: (response: TablePageEvent) => {
        this.odsList = response.content;
        this.totalRecords = response.totalElements;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.loading = false;
        this.spinnerService.hide();
        this.odsList = [];
        this.showError('No se pudieron cargar los datos. Intente nuevamente.');
      }
    });
  }


  viewOds(codigo: string): void {
    if (!codigo) {
      console.error('Código inválido:', codigo);
      return;
    }
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
    this.spinnerService.show();
    this.odsService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Registro eliminado correctamente',
          life: 3000
        });
        // Recarga la tabla después de eliminar
        this.loadOds({ first: this.first, rows: this.rows });
        this.spinnerService.hide();
      },
      error: (err) => {
        this.spinnerService.hide();
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