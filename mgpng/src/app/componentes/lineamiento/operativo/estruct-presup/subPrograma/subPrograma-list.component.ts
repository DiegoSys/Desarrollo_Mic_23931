import { Component, OnInit, Input } from '@angular/core';
import { SubProgramaService } from './subPrograma.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { SubProgramaModel, TablePageEvent } from './models/subPrograma.model';

@Component({
  selector: 'app-subprograma-list',
  templateUrl: './subPrograma-list.component.html',
  styleUrls: ['./subPrograma-list.component.scss']
})
export class SubProgramaListComponent implements OnInit {
  @Input() codigoPrograma: number | string = '';

  subProgramaList: SubProgramaModel[] = [];
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
        handler: (item) => this.viewSubPrograma(item.id)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editSubPrograma(item.id)
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
    rowTrackBy: 'id',
    globalFilterFields: ['codigo', 'nombre', 'descripcion']
  };

  constructor(
    private subProgramaService: SubProgramaService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.loadSubProgramaPorPrograma({ first: 0, rows: 10 });
  }


  // Método para actualizar los filtros y recargar la tabla
  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadSubProgramaPorPrograma({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadSubProgramaPorPrograma({ first: 0, rows: 10 });
  }

  loadSubProgramaPorPrograma(event: TablePageEvent = { first: 0, rows: 10 }): void {
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

    // Asegúrate de que codigoPrograma es un id numérico
    const programaId = Number(this.codigoPrograma);
    if (!programaId) {
      this.subProgramaList = [];
      this.totalRecords = 0;
      this.loading = false;
      this.spinnerService.hide();
      return;
    }

    this.subProgramaService.getByProgramaIdAndEstado(programaId, params).subscribe({
      next: (response) => {
        this.subProgramaList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar subprogramas por programa',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  // ...existing code...

  existeSubProgramaDefault(): boolean {
    return this.subProgramaList.some(
      sp =>
        sp.codigo === '00' &&
        sp.nombre === 'N/A' &&
        sp.descripcion === 'N/A'
    );
  }

  onPageChange(event: TablePageEvent): void {
    this.loadSubProgramaPorPrograma(event);
  }

  createSubPrograma(): void {
    this.router.navigate(
      ['/lineamiento/operativo/estruct-presup/subPrograma/new'],
      { queryParams: { codigoPrograma: this.codigoPrograma } }
    );
  }

  viewSubPrograma(id: number): void {
    if (!id) return;
    this.router.navigate(
      ['/lineamiento/operativo/estruct-presup/subPrograma/view'],
      { queryParams: { id, codigoPrograma: this.codigoPrograma } }
    );
  }

  editSubPrograma(id: number): void {
    if (!id) return;
    this.router.navigate(
      ['/lineamiento/operativo/estruct-presup/subPrograma/edit'],
      { queryParams: { id, codigoPrograma: this.codigoPrograma } }
    );
  }

  confirmDelete(id: number): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el subprograma?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteSubPrograma(id)
    });
  }

  deleteSubPrograma(id: number): void {
    this.loading = true;
    this.spinnerService.show();
    this.subProgramaService.delete(id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Subprograma eliminado correctamente',
          life: 5000
        });
        this.loadSubProgramaPorPrograma();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el subprograma',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  crearDefault(): void {
    const programaId = Number(this.codigoPrograma);
    if (!programaId) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Advertencia',
        detail: 'No hay id de programa disponible'
      });
      return;
    }
    this.loading = true;
    this.spinnerService.show();
    this.subProgramaService.createDefault(programaId).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Subprograma default creado correctamente',
          life: 5000
        });
        this.loadSubProgramaPorPrograma();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo crear el subprograma default',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}