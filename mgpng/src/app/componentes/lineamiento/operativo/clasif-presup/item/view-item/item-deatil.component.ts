import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ItemService } from '../item.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ItemModel } from '../models/item.model';

@Component({
  selector: 'app-item-detail',
  templateUrl: './item-detail.component.html'
})
export class ItemDetailComponent implements OnInit {
  item: ItemModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private itemService: ItemService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadItem(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.item) return;

    this.viewFields = [
      {
        key: 'nombre',
        label: 'Nombre',
        value: this.item.nombre,
        type: 'text',
        order: 1
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.item.descripcion,
        type: 'multiline',
        order: 2,
        width: 'full'
      }
    ];
  }

  loadItem(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.itemService.getById(codigo).subscribe({
      next: (item) => {
        this.item = item;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Item'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.item = null;
      }
    });
  }

  onCancel(): void {
    const codigoSubGrupo = this.route.snapshot.queryParams['codigoSubGrupo'];
    const codigoGrupo = this.route.snapshot.queryParams['codigoGrupo'];
    const codigoNaturaleza = this.route.snapshot.queryParams['codigoNaturaleza'];
    this.router.navigate(['/lineamiento/operativo/clasif-presup/subGrupo/view'], {
      queryParams: {
        codigo: codigoSubGrupo,
        codigoGrupo: codigoGrupo,
        codigoNaturaleza: codigoNaturaleza
      }
    });
  }
}