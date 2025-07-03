import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { EjeService } from '../eje.service';
import { EjeModel } from '../models/eje.model';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';

@Component({
  selector: 'app-eje-detail',
  templateUrl: './eje-detail.component.html'
})
export class EjeDetailComponent implements OnInit {
  eje: EjeModel | null = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ejeService: EjeService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadEje(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.eje) return;
    
    this.viewFields = [
      {
        key: 'codigoPlanNacionalFk',
        label: 'Plan Nacional Relacionado',
        value: this.eje.codigoPlanNacionalFk,
        type: 'text',
        order: 1
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.eje.alineacion,
        type: 'text',
        order: 2
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.eje.descripcion,
        type: 'multiline',
        order: 3,
        width: 'full'
      }
    ];
  }

  loadEje(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;
    
    this.ejeService.getById(codigo).subscribe({
      next: (eje) => {
        this.eje = eje;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Eje:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Eje'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.eje = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}
