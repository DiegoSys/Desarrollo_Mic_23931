import { Component, OnInit, Input } from '@angular/core';
import { GrupoService } from './grupo.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { GrupoModel, TablePageEvent } from './models/grupo.model';

@Component({
  selector: 'app-grupo-list',
  templateUrl: './grupo-list.component.html',
  styleUrls: ['./grupo-list.component.scss']
})
export class GrupoListComponent implements OnInit {
  @Input() codigoNaturaleza: string = '';

  grupoList: GrupoModel[] = [];
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
        handler: (item) => this.viewGrupo(item.codigo)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editGrupo(item.codigo)
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
    private grupoService: GrupoService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.loadGrupoPorNaturaleza({ first: 0, rows: 10 });
  }


  // Método para actualizar los filtros y recargar la tabla
  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadGrupoPorNaturaleza({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadGrupoPorNaturaleza({ first: 0, rows: 10 });
  }

  loadGrupoPorNaturaleza(event: TablePageEvent = { first: 0, rows: 10 }): void {
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

    if (!this.codigoNaturaleza) {
      this.grupoList = [];
      this.totalRecords = 0;
      this.loading = false;
      this.spinnerService.hide();
      return;
    }

    this.grupoService.getByNaturaleza(this.codigoNaturaleza, params).subscribe({
      next: (response) => {
        this.grupoList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar grupos por naturaleza',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  onPageChange(event: TablePageEvent): void {
    this.loadGrupoPorNaturaleza(event);
  }

  createGrupo(): void {
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/grupo/new'],
      { queryParams: { codigoNaturaleza: this.codigoNaturaleza } }
    );
  }

  viewGrupo(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/grupo/view'],
      { queryParams: { codigo, codigoNaturaleza: this.codigoNaturaleza } }
    );
  }

  editGrupo(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/grupo/edit'],
      { queryParams: { codigo, codigoNaturaleza: this.codigoNaturaleza } }
    );
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el grupo ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteGrupo(codigo)
    });
  }

  deleteGrupo(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.grupoService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Grupo eliminado correctamente',
          life: 5000
        });
        this.loadGrupoPorNaturaleza();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el grupo',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}