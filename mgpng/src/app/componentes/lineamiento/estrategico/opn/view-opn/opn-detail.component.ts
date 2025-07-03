import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { OpnService } from '../opn.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { OpnModel } from '../models/opn.model';

@Component({
  selector: 'app-opn-detail',
  templateUrl: './opn-detail.component.html'
})
export class OpnDetailComponent implements OnInit {
  opn: OpnModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private opnService: OpnService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadOpn(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.opn) return;

    this.viewFields = [
      {
        key: 'codigoPlanNacionalFk',
        label: 'Plan Nacional Relacionado',
        value: this.opn.codigoPlanNacionalFk,
        type: 'text',
        order: 1
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.opn.alineacion,
        type: 'text',
        order: 2
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.opn.descripcion,
        type: 'multiline',
        order: 3,
        width: 'full'
      }
    ];
  }

  loadOpn(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.opnService.getById(codigo).subscribe({
      next: (opn) => {
        this.opn = opn;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading OPN:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Objetivo Nacional'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.opn = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}