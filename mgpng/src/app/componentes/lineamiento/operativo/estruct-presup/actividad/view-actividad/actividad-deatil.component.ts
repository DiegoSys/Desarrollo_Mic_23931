import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ActividadService } from '../actividad.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ActividadModel } from '../models/actividad.model';

@Component({
  selector: 'app-actividad-detail',
  templateUrl: './actividad-detail.component.html'
})
export class ActividadDetailComponent implements OnInit {
  actividad: ActividadModel | null = null;
  loading = false;

  viewFields: ViewField[] = [];

  proyectoId?: number;
  subProgramaId?: number;
  programaId?: number;
  actividadId?: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private actividadService: ActividadService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.actividadId = params['id'] ? Number(params['id']) : undefined;
      this.proyectoId = params['proyectoId'] ? Number(params['proyectoId']) : undefined;
      this.subProgramaId = params['subProgramaId'] ? Number(params['subProgramaId']) : undefined;
      this.programaId = params['programaId'] ? Number(params['programaId']) : undefined;
      // Solo requiere actividadId para cargar
      if (this.actividadId) {
        this.loadActividad(this.actividadId);
      }
    });
  }

  setupViewFields(): void {
    if (!this.actividad) return;

    this.viewFields = [
      {
        key: 'nombre',
        label: 'Nombre',
        value: this.actividad.nombre,
        type: 'text',
        order: 1
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.actividad.descripcion,
        type: 'multiline',
        order: 2,
        width: 'full'
      }
    ];
  }

  loadActividad(id: number): void {
    this.spinnerService.show();
    this.loading = true;

    this.actividadService.getById(id).subscribe({
      next: (actividad) => {
        this.actividad = actividad;
        this.setupViewFields();
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar la Actividad'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.actividad = null;
      }
    });
  }

  onBack(): void {
    // Si no hay proyectoId en la URL pero la actividad sí tiene, úsalo
    const proyectoId = this.proyectoId ?? this.actividad?.proyectoId;
    this.router.navigate(['/lineamiento/operativo/estruct-presup/proyecto/view'], {
      queryParams: {
        id: proyectoId,
        programaId: this.programaId,
        subProgramaId: this.subProgramaId
      }
    });
  }
}