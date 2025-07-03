import { Component, OnInit } from '@angular/core';
import { OpnService } from './opn.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { TablePageEvent, OpnModel } from './models/opn.model';

@Component({
  selector: 'app-opn-list',
  templateUrl: './opn-list.component.html',
  styleUrls: ['./opn-list.component.scss']
})
export class OpnListComponent implements OnInit {
  opnList: OpnModel[] = [];
  totalRecords = 0;
  loading = true;

  sortField: string;
  sortOrder: string;
  searchCriteria: { [key: string]: string } = {}; // Nuevo campo para filtros

  tableConfig: TableConfig = {
    columns: [
      { header: 'Código', field: 'codigo', type: 'text' },
      { header: 'Código Plan Nacional', field: 'codigoPlanNacionalFk', type: 'text' },
      { header: 'Descripción', field: 'descripcion', type: 'text' },
      { header: 'Fecha Creación', field: 'fechaCreacion', type: 'date', format: 'dd/MM/yyyy' }
    ],
    actions: [
      {
        icon: 'pi pi-eye',
        label: '',
        handler: (item) => this.viewOpn(item.codigo)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editOpn(item.codigo)
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
    private opnService: OpnService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.loading = true;
    this.loadOpn({ first: 0, rows: 10 });
  }


  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadOpn({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }): void {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadOpn({ first: 0, rows: 10 });
  }

  loadOpn(event: TablePageEvent = { first: 0, rows: 10 }): void {
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

    this.opnService.getAll(params).subscribe({
      next: (response) => {
        this.opnList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar objetivos nacionales',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }


  onPageChange(event: TablePageEvent): void {
    this.loadOpn(event);
  }

  createOpn(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewOpn(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { codigo } });
  }

  editOpn(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { codigo } });
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el objetivo nacional ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteOpn(codigo)
    });
  }

  deleteOpn(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.opnService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Objetivo nacional eliminado correctamente',
          life: 5000
        });
        this.loadOpn();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar objetivo nacional',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}