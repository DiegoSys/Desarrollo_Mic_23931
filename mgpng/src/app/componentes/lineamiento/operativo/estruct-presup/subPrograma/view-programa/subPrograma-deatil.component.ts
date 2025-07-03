import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { SubProgramaService } from '../subPrograma.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { SubProgramaModel } from '../models/subPrograma.model';
import { ProyectoModel } from '../../proyecto/models/proyecto.model';
@Component({
  selector: 'app-subprograma-detail',
  templateUrl: './subPrograma-detail.component.html'
})
export class SubProgramaDetailComponent implements OnInit {
  subPrograma: SubProgramaModel = null;
  loading = false;

  viewFields: ViewField[] = [];
  proyectoList: ProyectoModel[] = [];
  codigoPrograma: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private subProgramaService: SubProgramaService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const id = params['id'];
      const codigoPrograma = params['codigoPrograma'];
      if (id && codigoPrograma) {
        this.codigoPrograma = Number(codigoPrograma);
        this.loadSubPrograma(Number(id));
      }
    });
  }

  setupViewFields(): void {
    if (!this.subPrograma) return;

    this.viewFields = [
      {
        key: 'nombre',
        label: 'Nombre',
        value: this.subPrograma.nombre,
        type: 'text',
        order: 1
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.subPrograma.descripcion,
        type: 'multiline',
        order: 2,
        width: 'full'
      }
    ];
  }

  loadSubPrograma(id: number): void {
    this.spinnerService.show();
    this.loading = true;

    this.subProgramaService.getById(id).subscribe({
      next: (subPrograma) => {
        this.subPrograma = subPrograma;
        this.setupViewFields();
        this.proyectoList = subPrograma.proyectos || [];
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Subprograma'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.subPrograma = null;
        this.proyectoList = [];
      }
    });
  }

  onCancel(): void {
    const programaId = this.route.snapshot.queryParamMap.get('codigoPrograma');
    this.router.navigate(['/lineamiento/operativo/estruct-presup/programa/view'], {
      queryParams: { id: programaId }
    });
  }
}