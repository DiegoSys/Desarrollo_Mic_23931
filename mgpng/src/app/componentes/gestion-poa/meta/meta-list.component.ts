import { Component, OnInit } from '@angular/core';
import { MetaService } from './meta.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { MetaModel, TablePageEvent } from './models/meta.model';

@Component({
  selector: 'app-meta-list',
  templateUrl: './meta-list.component.html',
  styleUrls: ['./meta-list.component.scss']
})
export class MetaListComponent implements OnInit {
  metaList: MetaModel[] = [];
  totalRecords = 0;
  loading = true;

  tableConfig: TableConfig = {
    columns: [
      { header: 'Código', field: 'codigo', type: 'text' },
      { header: 'Objetivo Nacional Relacionado', field: 'codigoOpnFk', type: 'text' },
      { header: 'Descripción', field: 'descripcion', type: 'text' },
      { header: 'Fecha Creación', field: 'fechaCreacion', type: 'date', format: 'dd/MM/yyyy' }
    ],
    actions: [
      { 
        icon: 'pi pi-eye', 
        label: '', 
        handler: (item) => this.viewMeta(item.codigo) 
      },
      { 
        icon: 'pi pi-pencil', 
        label: '', 
        handler: (item) => this.editMeta(item.codigo) 
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
    private metaService: MetaService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.loadMeta({ first: 0, rows: 10 });
  }

  loadMeta(event: TablePageEvent = { first: 0, rows: 10 }): void {
    this.loading = true;
    this.spinnerService.show();

    const params = {
      page: Math.floor((event.first || 0) / (event.rows || 10)),
      size: event.rows || 10
    };

    this.metaService.getAll(params).subscribe({
      next: (response) => {
        this.metaList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar metas',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  onPageChange(event: TablePageEvent): void {
    this.loadMeta(event);
  }

  createMeta(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewMeta(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { codigo } });
  }

  editMeta(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { codigo } });
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar la meta ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteMeta(codigo)
    });
  }

  deleteMeta(codigo: string): void {
    this.spinnerService.show();
    this.metaService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Meta eliminada correctamente',
          life: 5000
        });
        this.loadMeta();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar la meta',
          life: 5000
        });
        this.spinnerService.hide();
      }
    });
  }
}