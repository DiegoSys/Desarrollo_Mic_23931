import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { PdnService } from '../pdn.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { PdnModel } from '../models/pdn.model';

@Component({
  selector: 'app-pdn-detail',
  templateUrl: './pdn-detail.component.html'
})
export class PdnDetailComponent implements OnInit {
  pdn: PdnModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private pdnService: PdnService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadPdn(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.pdn) return;

    this.viewFields = [
      {
        key: 'codigoOpnFk',
        label: 'Objetivo Nacional Relacionado',
        value: this.pdn.codigoOpnFk,
        type: 'text',
        order: 1
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.pdn.alineacion,
        type: 'text',
        order: 2
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.pdn.descripcion,
        type: 'multiline',
        order: 3,
        width: 'full'
      }
    ];
  }

  loadPdn(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.pdnService.getById(codigo).subscribe({
      next: (pdn) => {
        this.pdn = pdn;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading PDN:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar la Política del PDN'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.pdn = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}