import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { PnService } from '../pn.service';
import { PnModel } from '../models/pn.model';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';

@Component({
  selector: 'app-pn-detail',
  templateUrl: './pn-detail.component.html'
})
export class PnDetailComponent implements OnInit {
  pn: PnModel | null = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private pnService: PnService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadPn(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.pn) return;
    
    this.viewFields = [
      {
        key: 'codigo',
        label: 'Código',
        value: this.pn.codigo,
        type: 'text',
        order: 1
      },
      {
        key: 'codigoObjDessostFk',
        label: 'ODS Relacionado',
        value: this.pn.codigoObjDessostFk,
        type: 'text',
        order: 2
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.pn.alineacion,
        type: 'text',
        order: 3
      },
      {
        key: 'estado',
        label: 'Estado',
        value: this.pn.estado === 'A',
        type: 'status',
        config: {
          status: {
            activeValue: true,
            activeLabel: 'Activo',
            inactiveValue: false,
            inactiveLabel: 'Inactivo'
          }
        },
        order: 4
      },
      {
        key: 'fechaInicio',
        label: 'Fecha Inicio',
        value: new Date(this.pn.fechaInicio).toLocaleDateString(),
        type: 'text',
        order: 5
      },
      {
        key: 'fechaFin',
        label: 'Fecha Fin',
        value: new Date(this.pn.fechaFin).toLocaleDateString(),
        type: 'text',
        order: 6
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.pn.descripcion,
        type: 'multiline',
        order: 7,
        width: 'full'
      }
    ];
  }

  loadPn(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;
    
    this.pnService.getById(codigo).subscribe({
      next: (pn) => {
        this.pn = pn;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading PN:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el PN'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.pn = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}
