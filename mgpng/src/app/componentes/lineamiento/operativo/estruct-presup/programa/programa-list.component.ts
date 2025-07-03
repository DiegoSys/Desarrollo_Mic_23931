import { Component, OnInit } from '@angular/core';
import { ProgramaService } from './programa.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProgramaModel, TablePageEvent } from './models/programa.model';

@Component({
  selector: 'app-programa-list',
  templateUrl: './programa-list.component.html',
  styleUrls: ['./programa-list.component.scss']
})
export class ProgramaListComponent implements OnInit {
  programaList: ProgramaModel[] = [];
  totalRecords = 0;
  loading = true;

  sortField: string;
  sortOrder: string;
  searchCriteria: { [key: string]: string } = {}; // Nuevo campo para filtros

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
        handler: (item) => this.viewPrograma(item.id) // Cambiado a item.id
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editPrograma(item.id) // Cambiado a item.id
      },
      {
        icon: 'pi pi-trash',
        label: '',
        handler: (item) => this.confirmDelete(item.id),
        color: 'p-button-danger'
      }
    ],
    pagination: true,
    rowsPerPage: [10, 25, 50],
    showCurrentPageReport: true,
    selectionMode: undefined,
    rowTrackBy: 'id', // Cambiado a 'id' para unicidad
    globalFilterFields: ['codigo', 'nombre', 'descripcion']
  };

  constructor(
    private programaService: ProgramaService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.loadPrograma({ first: 0, rows: 10 });
  }


  // Método para actualizar los filtros y recargar la tabla
  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadPrograma({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadPrograma({ first: 0, rows: 10 });
  }

  loadPrograma(event: TablePageEvent = { first: 0, rows: 10 }): void {
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

    this.programaService.getAll(params).subscribe({
      next: (response) => {
        this.programaList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar programas',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  // ...existing code...

  onPageChange(event: TablePageEvent): void {
    this.loadPrograma(event);
  }

  createPrograma(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewPrograma(id: number): void {
    if (!id) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { id } });
  }

  editPrograma(id: number): void {
    if (!id) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { id } });
  }

  confirmDelete(id: number): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el programa?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deletePrograma(id)
    });
  }

  deletePrograma(id: number): void {
    this.loading = true;
    this.spinnerService.show();
    this.programaService.delete(id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Programa eliminado correctamente',
          life: 5000
        });
        this.loadPrograma();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el programa',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}