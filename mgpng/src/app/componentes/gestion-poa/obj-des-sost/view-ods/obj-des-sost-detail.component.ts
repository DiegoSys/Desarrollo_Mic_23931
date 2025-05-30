import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { OdsService } from '../ods.service';
import { OdsModel } from '../models/obj-des-sost.model';

interface CustomAction {
  label: string;
  icon: string;
  handler: () => void;
  visible?: boolean;
  class?: string;
}
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';

@Component({
  selector: 'app-obj-des-sost-detail',
  templateUrl: './obj-des-sost-detail.component.html'
})
export class ObjDesSostDetailComponent implements OnInit {
  ods: OdsModel | null = null;
  loading = false;
  loadingTemplate = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private odsService: OdsService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  viewFields: ViewField[] = [];

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadOds(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.ods) return;
    
    this.viewFields = [
      {
        key: 'codigo',
        label: 'Código',
        value: this.ods.codigo,
        type: 'text',
        order: 1
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.ods.alineacion,
        type: 'text',
        order: 2
      },
      {
        key: 'estado',
        label: 'Estado',
        value: this.ods.estado === 'A',
        type: 'status',
        config: {
          status: {
            activeValue: true,
            activeLabel: 'Activo',
            inactiveValue: false,
            inactiveLabel: 'Inactivo'
          }
        },
        order: 3
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.ods.descripcion,
        type: 'multiline',
        order: 4,
        width: 'full'
      }
    ];
  }

  loadOds(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;
    
    this.odsService.getById(codigo).subscribe({
      next: (ods) => {
        this.ods = ods;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading ODS:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el ODS'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.ods = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }


}
