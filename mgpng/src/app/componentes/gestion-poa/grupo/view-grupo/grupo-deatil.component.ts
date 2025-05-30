import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { GrupoService } from '../grupo.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { GrupoModel } from '../models/grupo.model';

@Component({
  selector: 'app-grupo-detail',
  templateUrl: './grupo-detail.component.html'
})
export class GrupoDetailComponent implements OnInit {
  grupo: GrupoModel = null;
  loading = false;

  viewFields: ViewField[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private grupoService: GrupoService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadGrupo(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.grupo) return;

    this.viewFields = [
      {
        key: 'codigo',
        label: 'Código',
        value: this.grupo.codigo,
        type: 'text',
        order: 1
      },
      {
        key: 'nombre',
        label: 'Nombre',
        value: this.grupo.nombre,
        type: 'text',
        order: 2
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.grupo.descripcion,
        type: 'multiline',
        order: 3,
        width: 'full'
      }
    ];
  }

  loadGrupo(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.grupoService.getById(codigo).subscribe({
      next: (grupo) => {
        this.grupo = grupo;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Grupo:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Grupo'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.grupo = null;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}