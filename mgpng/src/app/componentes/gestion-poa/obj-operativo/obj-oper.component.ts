import { Component, OnInit } from '@angular/core';
import { ObjOperService } from './obj-oper.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ObjOperModel, TablePageEvent } from './models/obj-oper.model';

@Component({
  selector: 'app-obj-oper-list',
  templateUrl: './obj-oper.component.html',
  styleUrls: ['./obj-oper.component.scss']
})
export class ObjOperListComponent implements OnInit {
  objOperList: ObjOperModel[] = [];
  totalRecords = 0;
  loading = true;

  tableConfig: TableConfig = {
    columns: [
      { header: 'Código', field: 'codigo', type: 'text' },
      { header: 'Objetivo Estratégico', field: 'codigoEstrFk', type: 'text' },
      { header: 'Descripción', field: 'descripcion', type: 'text' },
      { header: 'Fecha Creación', field: 'fechaCreacion', type: 'date', format: 'dd/MM/yyyy' }
    ],
    actions: [
      { 
        icon: 'pi pi-eye', 
        label: '', 
        handler: (item) => this.viewObjOper(item.codigo) 
      },
      { 
        icon: 'pi pi-pencil', 
        label: '', 
        handler: (item) => this.editObjOper(item.codigo) 
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
    private objOperService: ObjOperService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.loadObjOper({ first: 0, rows: 10 });
  }

  loadObjOper(event: TablePageEvent = { first: 0, rows: 10 }): void {
    this.loading = true;
    this.spinnerService.show();

    const params = {
      page: Math.floor((event.first || 0) / (event.rows || 10)),
      size: event.rows || 10
    };

    this.objOperService.getAll(params).subscribe({
      next: (response) => {
        this.objOperList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar objetivos operativos',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }

  onPageChange(event: TablePageEvent): void {
    this.loadObjOper(event);
  }

  createObjOper(): void {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  viewObjOper(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['view'], { relativeTo: this.route, queryParams: { codigo } });
  }

  editObjOper(codigo: string): void {
    if (!codigo) return;
    this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { codigo } });
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el objetivo operativo ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteObjOper(codigo)
    });
  }

  deleteObjOper(codigo: string): void {
    this.spinnerService.show();
    this.objOperService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Objetivo operativo eliminado correctamente',
          life: 5000
        });
        this.loadObjOper();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el objetivo operativo',
          life: 5000
        });
        this.spinnerService.hide();
      }
    });
  }
}