import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ActividadService } from './actividad.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ActividadModel, TablePageEvent } from './models/actividad.model';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-actividad-list',
  templateUrl: './actividad-list.component.html',
  styleUrls: ['./actividad-list.component.scss']
})
export class ActividadListComponent implements OnInit, OnChanges {
  @Input() proyectoId?: number;
  @Input() subProgramaId?: number;
  @Input() programaId?: number;
  @Input() data: ActividadModel[] | null = null;

  actividadList: ActividadModel[] = [];
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
        handler: (item) => this.viewActividad(item.id)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editActividad(item.id)
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
    private actividadService: ActividadService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService,
    private cdr: ChangeDetectorRef

  ) {}

  ngOnInit(): void {
      console.log('ngOnInit ActividadListComponent');

    // Si los @Input no vienen definidos, toma los parámetros del path igual que en ProyectoListComponent
    if (!this.proyectoId) {
      this.proyectoId = Number(this.route.snapshot.queryParamMap.get('id'));
    }
    if (!this.programaId) {
      this.programaId = Number(this.route.snapshot.queryParamMap.get('programaId'));
    }
    if (!this.subProgramaId) {
      this.subProgramaId = Number(this.route.snapshot.queryParamMap.get('subProgramaId'));
    }

    if (this.data) {
      this.actividadList = this.data;
      this.totalRecords = this.data.length;
      this.loading = false;
    } else {
      this.loadActividad({ first: 0, rows: 10 });
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
      console.log('ngOnChanges ActividadListComponent', changes);

    if (changes['data'] && this.data) {
      this.actividadList = this.data;
      this.totalRecords = this.data.length;
      this.loading = false;
    }
    if ((changes['proyectoId']) && !this.data) {
      this.loadActividad({ first: 0, rows: 10 });
    }
  }
  
    // Método para actualizar los filtros y recargar la tabla
    onSearch(criteria: { [key: string]: string }): void {
      this.searchCriteria = criteria;
      this.loadActividad({ first: 0, rows: 10 });
    }
  
    onSortChange(event: { sortField: string, sortOrder: string }) {
      this.sortField = event.sortField;
      this.sortOrder = event.sortOrder;
      this.loadActividad({ first: 0, rows: 10 });
    }
  
    loadActividad(event: TablePageEvent = { first: 0, rows: 10 }): void {
      if (this.data && this.data.length > 0) {
        this.actividadList = this.data;
        this.totalRecords = this.data.length;
        this.loading = false;
        this.cdr.detectChanges();
        return;
      }
  
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
  
      if (!this.proyectoId) {
        this.actividadList = [];
        this.totalRecords = 0;
        this.loading = false;
        this.spinnerService.hide();
        this.cdr.detectChanges();
        return;
      }
  
      this.actividadService.getByProyectoPaginated(this.proyectoId, params).subscribe({
        next: (response) => {
          this.actividadList = response.content || [];
          this.totalRecords = response.totalElements || 0;
          this.loading = false;
          this.spinnerService.hide();
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Error al cargar actividades',
            life: 5000
          });
          this.loading = false;
          this.spinnerService.hide();
          this.cdr.detectChanges();
        }
      });
    }
  
  // ...existing code...

  onPageChange(event: TablePageEvent): void {
    this.loadActividad(event);
  }

  createActividad(): void {
    this.router.navigate(
      ['/lineamiento/operativo/estruct-presup/actividad/new'],
      { queryParams: { 
        id: this.proyectoId,
        programaId: this.programaId,
        subProgramaId: this.subProgramaId
      }}
    );
  }

  viewActividad(id: number): void {
    if (!id) return;
    this.router.navigate(
      ['/lineamiento/operativo/estruct-presup/actividad/view'],
      { queryParams: { 
        id,
        proyectoId: this.proyectoId,
        programaId: this.programaId,
        subProgramaId: this.subProgramaId
      }}
    );
  }

  editActividad(id: number): void {
    if (!id) return;
    this.router.navigate(
      ['/lineamiento/operativo/estruct-presup/actividad/edit'],
      { queryParams: { 
        id,
        proyectoId: this.proyectoId,
        programaId: this.programaId,
        subProgramaId: this.subProgramaId
      }}
    );
  }

  confirmDelete(id: number): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar la actividad?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteActividad(id)
    });
  }

  deleteActividad(id: number): void {
    this.loading = true;
    this.spinnerService.show();
    this.actividadService.delete(id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Actividad eliminada correctamente',
          life: 5000
        });
        this.loadActividad();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar la actividad',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}