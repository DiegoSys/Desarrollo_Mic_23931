import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ProInsService } from '../prog-ins.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProInsModel } from '../models/prog-ins.model';

@Component({
  selector: 'app-prog-ins-detail',
  templateUrl: './prog-ins-detail.component.html'
})
export class ProgInsDetailComponent implements OnInit {
  progIns: ProInsModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private progInsService: ProInsService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadProgIns(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.progIns) return;

    this.viewFields = [
      {
        key: 'codigoMetaFk',
        label: 'Meta Relacionada',
        value: this.progIns.codigoMetaFk,
        type: 'text',
        order: 1
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.progIns.alineacion,
        type: 'text',
        order: 2
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.progIns.descripcion,
        type: 'multiline',
        order: 3,
        width: 'full'
      }
    ];
  }

  loadProgIns(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.progInsService.getById(codigo).subscribe({
      next: (progIns) => {
        this.progIns = progIns;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Programa Institucional:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Programa Institucional'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.progIns = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}