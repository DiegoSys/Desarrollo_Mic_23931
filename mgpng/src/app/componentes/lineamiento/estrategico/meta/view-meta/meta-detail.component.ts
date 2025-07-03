import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { MetaService } from '../meta.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { MetaModel } from '../models/meta.model';

@Component({
  selector: 'app-meta-detail',
  templateUrl: './meta-detail.component.html'
})
export class MetaDetailComponent implements OnInit {
  meta: MetaModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private metaService: MetaService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadMeta(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.meta) return;

    this.viewFields = [
      {
        key: 'codigoOpnFk',
        label: 'Objetivo Nacional Relacionado',
        value: this.meta.codigoOpnFk,
        type: 'text',
        order: 1
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.meta.alineacion,
        type: 'text',
        order: 2
    },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.meta.descripcion,
        type: 'multiline',
        order: 3,
        width: 'full'
      }
    ];
  }

  loadMeta(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.metaService.getById(codigo).subscribe({
      next: (meta) => {
        this.meta = meta;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Meta:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar la Meta'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.meta = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}