import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ProyectoService } from '../proyecto.service';
import { ActividadService } from '../../actividad/actividad.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProyectoModel } from '../models/proyecto.model';
import { ActividadModel } from '../../actividad/models/actividad.model';

@Component({
  selector: 'app-proyecto-detail',
  templateUrl: './proyecto-detail.component.html'
})
export class ProyectoDetailComponent implements OnInit {
  proyecto: ProyectoModel = null;
  loading = false;

  viewFields: ViewField[] = [];
  actividadList: ActividadModel[] = [];

  id: number;
  programaId: number;
  subProgramaId: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private proyectoService: ProyectoService,
    private actividadService: ActividadService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.id = Number(params['id']);
      this.programaId = Number(params['programaId']);
      this.subProgramaId = Number(params['subProgramaId']);
      if (this.id && this.programaId && this.subProgramaId) {
        this.loadProyecto(this.id);
      }
    });
  }

  setupViewFields(): void {
    if (!this.proyecto) return;

    this.viewFields = [
      {
        key: 'nombre',
        label: 'Nombre',
        value: this.proyecto.nombre,
        type: 'text',
        order: 1
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.proyecto.descripcion,
        type: 'multiline',
        order: 2,
        width: 'full'
      }
    ];
  }

  loadProyecto(id: number): void {
    this.spinnerService.show();
    this.loading = true;

    this.proyectoService.getById(id).subscribe({
      next: (proyecto) => {
        this.proyecto = proyecto;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Proyecto'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.proyecto = null;
        this.actividadList = [];
      }
    });
  }

  onBack(): void {
    this.router.navigate(['/lineamiento/operativo/estruct-presup/subPrograma/view'], {
      queryParams: { 
        id: this.subProgramaId, 
        codigoPrograma: this.programaId
      }
    });
  }
}