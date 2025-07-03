import { Component, OnInit } from '@angular/core';
import { PnlService } from './pnl.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { PnlModel, TablePageEvent } from './models/pnl.model';

@Component({
  selector: 'app-pnl-list',
  templateUrl: './pnl-list.component.html',
  styleUrls: ['./pnl-list.component.scss']
})
export class PnlListComponent implements OnInit {
  pnlList: PnlModel[] = [];
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
        handler: (item) => this.viewPnl(item.codigo)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editPnl(item.codigo)
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
    private pnlService: PnlService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.loading = true;
    this.loadPnl({ first: 0, rows: 10 });
  }


  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadPnl({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }): void {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadPnl({ first: 0, rows: 10 });
  }

  loadPnl(event: TablePageEvent = { first: 0, rows: 10 }): void {
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

    this.pnlService.getAll(params).subscribe({
      next: (response) => {
        this.pnlList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar programas nacionales',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }


  onPageChange(event: TablePageEvent): void {
    this.loadPnl(event);
  }

  createPnl(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewPnl(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { codigo } });
  }

  editPnl(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { codigo } });
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el programa nacional ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deletePnl(codigo)
    });
  }

  deletePnl(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.pnlService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Programa nacional eliminado correctamente',
          life: 5000
        });
        this.loadPnl();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el programa nacional',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}