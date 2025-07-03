import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { SubGrupoService } from '../subGrupo.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { SubGrupoModel } from '../models/subGrupo.model';
import { ItemModel } from './../../item/models/item.model';

@Component({
  selector: 'app-subgrupo-detail',
  templateUrl: './subGrupo-detail.component.html'
})
export class SubGrupoDetailComponent implements OnInit {
  subGrupo: SubGrupoModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  itemList: ItemModel[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private subGrupoService: SubGrupoService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadSubGrupo(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.subGrupo) return;

    this.viewFields = [
      {
        key: 'nombre',
        label: 'Nombre',
        value: this.subGrupo.nombre,
        type: 'text',
        order: 1
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.subGrupo.descripcion,
        type: 'multiline',
        order: 2,
        width: 'full'
      }
    ];
  }

  loadSubGrupo(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.subGrupoService.getById(codigo).subscribe({
      next: (subGrupo) => {
        this.subGrupo = subGrupo;
        this.setupViewFields();
        // Toma los items relacionados del modelo de subgrupo
        this.itemList = subGrupo.presItemList || [];
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Subgrupo'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.subGrupo = null;
        this.itemList = [];
      }
    });
  }

  onCancel(): void {
    // Obtiene el código de grupo de los query params y navega a la vista de grupo
    const codigoGrupo = this.route.snapshot.queryParamMap.get('codigoGrupo');
    const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/grupo/view'],
      { queryParams: { 
        codigo: codigoGrupo,
        codigoNaturaleza: codigoNaturaleza
        } }
    );
  }
}