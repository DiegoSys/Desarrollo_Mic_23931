import { Component, OnInit } from '@angular/core';
import { ObEstraService } from './ob-es.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ObEstraModel, TablePageEvent } from './models/ob-es.model';

@Component({
  selector: 'app-ob-es-list',
  templateUrl: './ob-es-list.component.html',
  styleUrls: ['./ob-es-list.component.scss']
})
export class ObEsListComponent implements OnInit {
  obEsList: ObEstraModel[] = [];
  totalRecords = 0;
  loading = true;

  sortField: string;
  sortOrder: string;
  searchCriteria: { [key: string]: string } = {}; // Nuevo campo para filtros

  tableConfig: TableConfig = {
    columns: [
      { header: 'Código', field: 'codigo', type: 'text' },
      { header: 'Código Meta', field: 'codigoMetaFk', type: 'text' },
      { header: 'Descripción', field: 'descripcion', type: 'text' },
      { header: 'Fecha Creación', field: 'fechaCreacion', type: 'date', format: 'dd/MM/yyyy' }
    ],
    actions: [
      {
        icon: 'pi pi-eye',
        label: '',
        handler: (item) => this.viewObEs(item.codigo)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editObEs(item.codigo)
      },
      {
        icon: 'pi pi-trash',
        label: '',
        handler: (item) => this.confirmDelete(item.codigo),
        color: 'p-button-danger'
      }
    ],
    pagination: true,
    rowsPerPage: [10, 25, 50],
    showCurrentPageReport: true,
    selectionMode: undefined,
    rowTrackBy: 'codigo',
    globalFilterFields: ['codigo', 'descripcion']
  };

  constructor(
    private obEsService: ObEstraService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.loading = true;
    this.loadObEs({ first: 0, rows: 10 });
  }


  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadObEs({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }): void {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadObEs({ first: 0, rows: 10 });
  }

  loadObEs(event: TablePageEvent = { first: 0, rows: 10 }): void {
    this.loading = true;
    this.spinnerService.show();

    const params: any = {
      page: Math.floor((event.first || 0) / (event.rows || 10)),
      size: event.rows || 10,
      sort: this.sortField,
      direction: this.sortOrder
    };

    if (Object.keys(this.searchCriteria).length > 0) {
      params.searchCriteria = this.searchCriteria;
    }

    this.obEsService.getAll(params).subscribe({
      next: (response) => {
        this.obEsList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar objetivos estratégicos',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }


  onPageChange(event: TablePageEvent): void {
    this.loadObEs(event);
  }

  createObEs(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewObEs(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { codigo } });
  }

  editObEs(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { codigo } });
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el objetivo estratégico ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteObEs(codigo)
    });
  }

  deleteObEs(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.obEsService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Objetivo estratégico eliminado correctamente',
          life: 5000
        });
        this.loadObEs();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el objetivo estratégico',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}