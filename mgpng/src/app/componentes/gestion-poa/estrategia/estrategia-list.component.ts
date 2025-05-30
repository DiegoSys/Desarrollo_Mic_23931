import { Component, OnInit } from '@angular/core';
import { EstrategiaService } from './estrategia.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { EstrategiaModel, TablePageEvent } from './models/estrategia.model';

@Component({
  selector: 'app-estrategia-list',
  templateUrl: './estrategia-list.component.html',
  styleUrls: ['./estrategia-list.component.scss']
})
export class EstrategiaListComponent implements OnInit {
  estrategiaList: EstrategiaModel[] = [];
  totalRecords = 0;
  loading = true;

  tableConfig: TableConfig = {
    columns: [
      { header: 'Código', field: 'codigo', type: 'text' },
      { header: 'Política PDN', field: 'codigoPdnFk', type: 'text' },
      { header: 'Descripción', field: 'descripcion', type: 'text' },
      { header: 'Fecha Creación', field: 'fechaCreacion', type: 'date', format: 'dd/MM/yyyy' }
    ],
    actions: [
      { 
        icon: 'pi pi-eye', 
        label: '', 
        handler: (item) => this.viewEstrategia(item.codigo) 
      },
      { 
        icon: 'pi pi-pencil', 
        label: '', 
        handler: (item) => this.editEstrategia(item.codigo) 
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
    private estrategiaService: EstrategiaService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.loadEstrategia({ first: 0, rows: 10 });
  }

  loadEstrategia(event: TablePageEvent = { first: 0, rows: 10 }): void {
    this.loading = true;
    this.spinnerService.show();

    const params = {
      page: Math.floor((event.first || 0) / (event.rows || 10)),
      size: event.rows || 10
    };

    this.estrategiaService.getAll(params).subscribe({
      next: (response) => {
        this.estrategiaList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar estrategias',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  onPageChange(event: TablePageEvent): void {
    this.loadEstrategia(event);
  }

  createEstrategia(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewEstrategia(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { codigo } });
  }

  editEstrategia(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { codigo } });
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar la estrategia ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteEstrategia(codigo)
    });
  }

  deleteEstrategia(codigo: string): void {
    this.spinnerService.show();
    this.estrategiaService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Estrategia eliminada correctamente',
          life: 5000
        });
        this.loadEstrategia();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar la estrategia',
          life: 5000
        });
        this.spinnerService.hide();
      }
    });
  }
}