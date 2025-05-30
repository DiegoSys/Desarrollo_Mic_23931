import { Component, OnInit } from '@angular/core';
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
  subGrupoList: SubGrupoModel[] = [];
  totalRecords = 0;
  loading = true;

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
  ) {}

  ngOnInit(): void {
    this.loadSubGrupos({ first: 0, rows: 10 });
  }

  loadSubGrupos(event: TablePageEvent = { first: 0, rows: 10 }): void {
    this.loading = true;
    this.spinnerService.show();

    const params = {
      page: Math.floor((event.first || 0) / (event.rows || 10)),
      size: event.rows || 10
    };

    this.subGrupoService.getAll(params).subscribe({
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
          detail: 'Error al cargar subgrupos',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  onPageChange(event: TablePageEvent): void {
    this.loadSubGrupos(event);
  }

  createSubGrupo(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewSubGrupo(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { codigo } });
  }

  editSubGrupo(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { codigo } });
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
    this.spinnerService.show();
    this.subGrupoService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Subgrupo eliminado correctamente',
          life: 5000
        });
        this.loadSubGrupos();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el subgrupo',
          life: 5000
        });
        this.spinnerService.hide();
      }
    });
  }
}
