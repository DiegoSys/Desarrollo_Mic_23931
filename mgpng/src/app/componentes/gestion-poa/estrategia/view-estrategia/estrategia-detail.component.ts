import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { EstrategiaService } from '../estrategia.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { EstrategiaModel } from '../models/estrategia.model';

@Component({
  selector: 'app-estrategia-detail',
  templateUrl: './estrategia-detail.component.html'
})
export class EstrategiaDetailComponent implements OnInit {
  estrategia: EstrategiaModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private estrategiaService: EstrategiaService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadEstrategia(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.estrategia) return;

    this.viewFields = [
      {
        key: 'codigo',
        label: 'Código',
        value: this.estrategia.codigo,
        type: 'text',
        order: 1
      },
      {
        key: 'codigoPdnFk',
        label: 'Política PDN Relacionada',
        value: this.estrategia.codigoPdnFk,
        type: 'text',
        order: 2
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.estrategia.alineacion,
        type: 'text',
        order: 3
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.estrategia.descripcion,
        type: 'multiline',
        order: 4,
        width: 'full'
      }
    ];
  }

  loadEstrategia(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.estrategiaService.getById(codigo).subscribe({
      next: (estrategia) => {
        this.estrategia = estrategia;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Estrategia:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar la Estrategia'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.estrategia = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}