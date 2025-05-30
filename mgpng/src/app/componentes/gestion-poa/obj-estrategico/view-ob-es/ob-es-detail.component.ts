import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ObEstraService } from '../ob-es.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ObEstraModel } from '../models/ob-es.model';

@Component({
  selector: 'app-ob-es-detail',
  templateUrl: './ob-es-detail.component.html'
})
export class ObEsDetailComponent implements OnInit {
  obEs: ObEstraModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private obEsService: ObEstraService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadObEs(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.obEs) return;

    this.viewFields = [
      {
        key: 'codigo',
        label: 'Código',
        value: this.obEs.codigo,
        type: 'text',
        order: 1
      },
      {
        key: 'codigoMetaFk',
        label: 'Meta Relacionada',
        value: this.obEs.codigoMetaFk,
        type: 'text',
        order: 2
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.obEs.alineacion,
        type: 'text',
        order: 3
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.obEs.descripcion,
        type: 'multiline',
        order: 4,
        width: 'full'
      }
    ];
  }

  loadObEs(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.obEsService.getById(codigo).subscribe({
      next: (obEs) => {
        this.obEs = obEs;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Objetivo Estratégico:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Objetivo Estratégico'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.obEs = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}