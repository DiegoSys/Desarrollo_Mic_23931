import { Component, OnInit } from '@angular/core';
import { PdnService } from './pdn.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { PdnModel, TablePageEvent } from './models/pdn.model';

@Component({
  selector: 'app-pdn-list',
  templateUrl: './pdn-list.component.html',
  styleUrls: ['./pdn-list.component.scss']
})
export class PdnListComponent implements OnInit {
  pdnList: PdnModel[] = [];
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
        handler: (item) => this.viewPdn(item.codigo) 
      },
      { 
        icon: 'pi pi-pencil', 
        label: '', 
        handler: (item) => this.editPdn(item.codigo) 
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
    private pdnService: PdnService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.loadPdn({ first: 0, rows: 10 });
  }

  loadPdn(event: TablePageEvent = { first: 0, rows: 10 }): void {
    this.loading = true;
    this.spinnerService.show();

    const params = {
      page: Math.floor((event.first || 0) / (event.rows || 10)),
      size: event.rows || 10
    };

    this.pdnService.getAll(params).subscribe({
      next: (response) => {
        this.pdnList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar objetivos del PDN',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  onPageChange(event: TablePageEvent): void {
    this.loadPdn(event);
  }

  createPdn(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewPdn(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { codigo } });
  }

  editPdn(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { codigo } });
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el objetivo del PDN ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deletePdn(codigo)
    });
  }

  deletePdn(codigo: string): void {
    this.spinnerService.show();
    this.pdnService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Objetivo del PDN eliminado correctamente',
          life: 5000
        });
        this.loadPdn();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar objetivo del PDN',
          life: 5000
        });
        this.spinnerService.hide();
      }
    });
  }
}