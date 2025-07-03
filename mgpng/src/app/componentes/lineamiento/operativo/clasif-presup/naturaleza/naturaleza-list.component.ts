import { Component, OnInit } from '@angular/core';
import { NaturalezaService } from './naturaleza.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { NaturalezaModel, TablePageEvent } from './models/naturaleza.model';

@Component({
  selector: 'app-naturaleza-list',
  templateUrl: './naturaleza-list.component.html',
  styleUrls: ['./naturaleza-list.component.scss']
})
export class NaturalezaListComponent implements OnInit {
  naturalezaList: NaturalezaModel[] = [];
  totalRecords = 0;
  loading = true;

  sortField: string;
  sortOrder: string;
  searchCriteria: { [key: string]: string } = {}; // Nuevo campo para filtros

  // Encabezados parametrizables
  encabezadoInstitucion = 'MINISTERIO DE ECONOMIA Y FINANZAS DEL ECUADOR';
  encabezadoSub = 'SUBSECRETARÍA DE PRESUPUESTO';
  encabezadoTitulo = 'CLASIFICADOR PRESUPUESTARIO DE INGRESOS Y EGRESOS DEL SECTOR PÚBLICO';
  encabezadoFecha = 'ACTUALIZADO AL 05 DE FEBRERO DE 2025 e-SIGEF';

  tableConfig: TableConfig = {
    columns: [
      { header: 'Código', field: 'codigo', type: 'text' },
      { header: 'Nombre', field: 'nombre', type: 'text' },
      { header: 'Descripción', field: 'descripcion', type: 'text' }
    ],
    actions: [
      {
        icon: 'pi pi-eye',
        label: '',
        handler: (item) => this.viewNaturaleza(item.codigo)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editNaturaleza(item.codigo)
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
    globalFilterFields: ['codigo', 'nombre', 'descripcion']
  };

  constructor(
    private naturalezaService: NaturalezaService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) { }



  ngOnInit(): void {
    this.loadNaturaleza({ first: 0, rows: 10 });
  }


  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadNaturaleza({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadNaturaleza({ first: 0, rows: 10 });
  }

  loadNaturaleza(event: TablePageEvent = { first: 0, rows: 10 }): void {
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

    this.naturalezaService.getAll(params).subscribe({
      next: (response) => {
        this.naturalezaList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar naturalezas',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  onPageChange(event: TablePageEvent): void {
    this.loadNaturaleza(event);
  }

  createNaturaleza(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewNaturaleza(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { codigo } });
  }

  editNaturaleza(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { codigo } });
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar la naturaleza ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteNaturaleza(codigo)
    });
  }

  deleteNaturaleza(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.naturalezaService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Naturaleza eliminada correctamente',
          life: 5000
        });
        this.loadNaturaleza();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar la naturaleza',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}