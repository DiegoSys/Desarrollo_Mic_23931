import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { PnlService } from '../pnl.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { PnlModel } from '../models/pnl.model';

@Component({
  selector: 'app-pnl-detail',
  templateUrl: './pnl-detail.component.html'
})
export class PnlDetailComponent implements OnInit {
  pnl: PnlModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private pnlService: PnlService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadPnl(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.pnl) return;

    this.viewFields = [
      {
        key: 'codigo',
        label: 'Código',
        value: this.pnl.codigo,
        type: 'text',
        order: 1
      },
      {
        key: 'codigoMetaFk',
        label: 'Meta Relacionada',
        value: this.pnl.codigoMetaFk,
        type: 'text',
        order: 2
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.pnl.alineacion,
        type: 'text',
        order: 3,
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.pnl.descripcion,
        type: 'multiline',
        order: 3,
        width: 'full'
      }
    ];
  }

  loadPnl(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.pnlService.getById(codigo).subscribe({
      next: (pnl) => {
        this.pnl = pnl;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Programa Nacional:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Programa Nacional'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.pnl = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}