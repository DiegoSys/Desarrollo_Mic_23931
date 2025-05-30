import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ProdInsService } from '../prod-inst.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProdInsModel } from '../models/prod-inst.model';

@Component({
  selector: 'app-prod-inst-detail',
  templateUrl: './prod-inst-detail.component.html'
})
export class ProdInstDetailComponent implements OnInit {
  prodIns: ProdInsModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private prodInsService: ProdInsService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadProdIns(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.prodIns) return;

    this.viewFields = [
      {
        key: 'codigo',
        label: 'Código',
        value: this.prodIns.codigo,
        type: 'text',
        order: 1
      },
      {
        key: 'codigoProginstFk',
        label: 'Programa Institucional Relacionado',
        value: this.prodIns.codigoProginstFk,
        type: 'text',
        order: 2
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.prodIns.alineacion,
        type: 'text',
        order: 3
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.prodIns.descripcion,
        type: 'multiline',
        order: 4,
        width: 'full'
      }
    ];
  }

  loadProdIns(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.prodInsService.getById(codigo).subscribe({
      next: (prodIns) => {
        this.prodIns = prodIns;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Producto Institucional:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Producto Institucional'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.prodIns = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}