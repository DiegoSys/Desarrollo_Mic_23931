import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ObjOperService } from '../obj-oper.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ObjOperModel } from '../models/obj-oper.model';

@Component({
  selector: 'app-obj-oper-detail',
  templateUrl: './obj-oper-detail.component.html'
})
export class ObjOperDetailComponent implements OnInit {
  objOper: ObjOperModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private objOperService: ObjOperService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadObjOper(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.objOper) return;

    this.viewFields = [
      {
        key: 'codigoEstrFk',
        label: 'Objetivo Estratégico Relacionado',
        value: this.objOper.codigoEstrFk,
        type: 'text',
        order: 1
      },
      {
        key: 'alineacion',
        label: 'Alineación',
        value: this.objOper.alineacion,
        type: 'text',
        order: 2
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.objOper.descripcion,
        type: 'multiline',
        order: 3,
        width: 'full'
      }
    ];
  }

  loadObjOper(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.objOperService.getById(codigo).subscribe({
      next: (objOper) => {
        this.objOper = objOper;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Objetivo Operativo:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Objetivo Operativo'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.objOper = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}