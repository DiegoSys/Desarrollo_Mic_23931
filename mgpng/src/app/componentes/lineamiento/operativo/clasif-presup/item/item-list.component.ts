import { Component, OnInit, Input } from '@angular/core';
import { ItemService } from './item.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ItemModel } from './models/item.model';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.scss']
})
export class ItemListComponent implements OnInit {
  @Input() codigoSubGrupo: string = '';

  itemList: ItemModel[] = [];
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
        handler: (item) => this.viewItem(item.codigo)
      },
      {
        icon: 'pi pi-pencil',
        label: '',
        handler: (item) => this.editItem(item.codigo)
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
    private itemService: ItemService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router,
    private route: ActivatedRoute,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.loadItemsPorSubGrupo({ first: 0, rows: 10 });
  }


  // Método para actualizar los filtros y recargar la tabla
  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadItemsPorSubGrupo({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadItemsPorSubGrupo({ first: 0, rows: 10 });
  }

  loadItemsPorSubGrupo(event: { first: number, rows: number }): void {
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

    if (!this.codigoSubGrupo) {
      this.itemList = [];
      this.totalRecords = 0;
      this.loading = false;
      this.spinnerService.hide();
      return;
    }

    this.itemService.getBySubGrupo(this.codigoSubGrupo, params).subscribe({
      next: (response) => {
        this.itemList = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar items por subgrupo',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }


  onPageChange(event: { first: number, rows: number }): void {
    this.loadItemsPorSubGrupo(event);
  }

  createItem(): void {
    const codigoGrupo = this.route.snapshot.queryParamMap.get('codigoGrupo');
    const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/item/new'],
      {
        queryParams: {
          codigoSubGrupo: this.codigoSubGrupo,
          codigoGrupo: codigoGrupo,
          codigoNaturaleza: codigoNaturaleza
        }
      }
    );
  }

  viewItem(codigo: string): void {
    if (!codigo) return;
    const codigoGrupo = this.route.snapshot.queryParamMap.get('codigoGrupo');
    const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/item/view'],
      {
        queryParams: {
          codigo,
          codigoSubGrupo: this.codigoSubGrupo,
          codigoGrupo: codigoGrupo,
          codigoNaturaleza: codigoNaturaleza
        }
      }
    );
  }

  editItem(codigo: string): void {
    if (!codigo) return;
    const codigoGrupo = this.route.snapshot.queryParamMap.get('codigoGrupo');
    const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/item/edit'],
      {
        queryParams: {
          codigo,
          codigoSubGrupo: this.codigoSubGrupo,
          codigoGrupo: codigoGrupo,
          codigoNaturaleza: codigoNaturaleza
        }
      }
    );
  }

  confirmDelete(codigo: string): void {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el item ${codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.deleteItem(codigo)
    });
  }

  deleteItem(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.itemService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Item eliminado correctamente',
          life: 5000
        });
        this.loadItemsPorSubGrupo({ first: 0, rows: 10 });
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar el item',
          life: 5000
        });
        this.loading = false;
        this.spinnerService.hide();
      }
    });
  }
}