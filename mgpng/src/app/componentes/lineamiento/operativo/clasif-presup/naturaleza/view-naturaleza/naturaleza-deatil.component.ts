import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { NaturalezaService } from '../naturaleza.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { NaturalezaModel } from '../models/naturaleza.model';
import { GrupoModel } from './../../grupo/models/grupo.model';

@Component({
  selector: 'app-naturaleza-detail',
  templateUrl: './naturaleza-detail.component.html'
})
export class NaturalezaDetailComponent implements OnInit {
  naturaleza: NaturalezaModel = null;
  loading = false;

  viewFields: ViewField[] = [];
  grupoList: GrupoModel[] = [];


  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private naturalezaService: NaturalezaService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const codigo = params['codigo'];
      if (codigo) {
        this.loadNaturaleza(codigo);
      }
    });
  }

  setupViewFields(): void {
    if (!this.naturaleza) return;

    this.viewFields = [
      {
        key: 'nombre',
        label: 'Nombre',
        value: this.naturaleza.nombre,
        type: 'text',
        order: 1
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.naturaleza.descripcion,
        type: 'multiline',
        order: 2,
        width: 'full'
      }
    ];
  }

  loadNaturaleza(codigo: string): void {
    this.spinnerService.show();
    this.loading = true;

    this.naturalezaService.getById(codigo).subscribe({
      next: (naturaleza) => {
        this.naturaleza = naturaleza;
        this.setupViewFields();
        // Toma los grupos directamente del modelo de naturaleza
        this.grupoList = naturaleza.presGrupoList || [];
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading Naturaleza:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar la Naturaleza'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.naturaleza = null;
        this.grupoList = [];
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}