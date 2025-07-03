import { Component, OnInit } from '@angular/core';
import { ProyectoService } from './proyecto.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProyectoModel, TablePageEvent } from './models/proyecto.model';

@Component({
  selector: 'app-proyecto-list',
  templateUrl: './proyecto-list.component.html',
  styleUrls: ['./proyecto-list.component.scss']
})
export class ProyectoListComponent implements OnInit {
  programaId: number;
  subProgramaId: number;

  proyectoList: ProyectoModel[] = [];
  totalRecords = 0;
  loading = true;

  sortField: string; // Campo por defecto para ordenar
  sortOrder: string; // Orden por defecto
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
        handler: (item) => this.viewProyecto(item.id)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editProyecto(item.id)
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
  };

  constructor(
    private proyectoService: ProyectoService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    // Solo toma los parámetros id numéricos
    this.programaId = Number(this.route.snapshot.queryParamMap.get('codigoPrograma'));
    this.subProgramaId = Number(this.route.snapshot.queryParamMap.get('id'));
    this.loadProyecto({ first: 0, rows: 10 });
  }

  crearDefault(): void {
    if (!this.programaId || !this.subProgramaId) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Advertencia',
        detail: 'No hay id de programa o subprograma disponible'
      });
      return;
    }
    this.loading = true;
    this.spinnerService.show();
    this.proyectoService.createDefault(this.programaId, this.subProgramaId).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Proyecto default creado correctamente',
          life: 5000
        });
        this.loadProyecto();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo crear el proyecto default',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

    loadProyecto(event: TablePageEvent = { first: 0, rows: 10 }): void {
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
  
    this.proyectoService.getByProgramaAndSubprogramaPaginated(this.programaId, this.subProgramaId, params).subscribe({
      next: (response) => {
        this.proyectoList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar proyectos',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  // Método para actualizar los filtros y recargar la tabla
  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadProyecto({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadProyecto({ first: 0, rows: 10 });
  }

  onPageChange(event: TablePageEvent): void {
    this.loadProyecto(event);
  }

  createProyecto(): void {
    this.router.navigate(
      ['/lineamiento/operativo/estruct-presup/proyecto/new'],
      { queryParams: { programaId: this.programaId, subProgramaId: this.subProgramaId } }
    );
  }

  viewProyecto(id: number): void {
    if (!id) return;
    this.router.navigate(
      ['/lineamiento/operativo/estruct-presup/proyecto/view'],
      { queryParams: { id, programaId: this.programaId, subProgramaId: this.subProgramaId } }
    );
  }

  editProyecto(id: number): void {
    if (!id) return;
    this.router.navigate(
      ['/lineamiento/operativo/estruct-presup/proyecto/edit'],
      { queryParams: { id, programaId: this.programaId, subProgramaId: this.subProgramaId } }
    );
  }

  confirmDelete(id: number): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el proyecto?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteProyecto(id)
    });
  }

  existeProyectoDefault(): boolean {
    return this.proyectoList.some(
      p =>
        p.codigo === '00' &&
        p.nombre === 'N/A' &&
        p.descripcion === 'N/A'
    );
  }

  deleteProyecto(id: number): void {
    this.loading = true;
    this.spinnerService.show();
    this.proyectoService.delete(id, this.programaId, this.subProgramaId).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Proyecto eliminado correctamente',
          life: 5000
        });
        this.loadProyecto();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el proyecto',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}