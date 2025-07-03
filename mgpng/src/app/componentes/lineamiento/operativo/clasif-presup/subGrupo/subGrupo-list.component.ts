import { Component, OnInit, Input } from '@angular/core';
import { SubGrupoService } from './subGrupo.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { SubGrupoModel, TablePageEvent } from './models/subGrupo.model';

@Component({
  selector: 'app-subGrupo-list',
  templateUrl: './subGrupo-list.component.html',
  styleUrls: ['./subGrupo-list.component.scss']
})
export class SubGrupoListComponent implements OnInit {
  @Input() codigoGrupo: string = '';

  subGrupoList: SubGrupoModel[] = [];
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
        handler: (item) => this.viewSubGrupo(item.codigo)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editSubGrupo(item.codigo)
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
    private subGrupoService: SubGrupoService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.loadSubGrupoPorGrupo({ first: 0, rows: 10 });
  }


  // Método para actualizar los filtros y recargar la tabla
  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadSubGrupoPorGrupo({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadSubGrupoPorGrupo({ first: 0, rows: 10 });
  }

  loadSubGrupoPorGrupo(event: TablePageEvent = { first: 0, rows: 10 }): void {
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

    if (!this.codigoGrupo) {
      this.subGrupoList = [];
      this.totalRecords = 0;
      this.loading = false;
      this.spinnerService.hide();
      return;
    }

    this.subGrupoService.getByGrupo(this.codigoGrupo, params).subscribe({
      next: (response) => {
        this.subGrupoList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar subgrupos por grupo',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }


  onPageChange(event: TablePageEvent): void {
    this.loadSubGrupoPorGrupo(event);
  }

  createSubGrupo(): void {
    const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/subGrupo/new'],
      {
        queryParams: {
          codigoGrupo: this.codigoGrupo,
          codigoNaturaleza: codigoNaturaleza
        }
      }
    );
  }

  viewSubGrupo(codigo: string): void {
    if (!codigo) return;
    const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/subGrupo/view'],
      {
        queryParams: {
          codigo,
          codigoGrupo: this.codigoGrupo,
          codigoNaturaleza: codigoNaturaleza
        }
      }
    );
  }

  editSubGrupo(codigo: string): void {
    if (!codigo) return;
    const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/subGrupo/edit'],
      {
        queryParams: {
          codigo,
          codigoGrupo: this.codigoGrupo,
          codigoNaturaleza: codigoNaturaleza
        }
      }
    );
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el subgrupo ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteSubGrupo(codigo)
    });
  }

  deleteSubGrupo(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.subGrupoService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Subgrupo eliminado correctamente',
          life: 5000
        });
        this.loadSubGrupoPorGrupo();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el subgrupo',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}