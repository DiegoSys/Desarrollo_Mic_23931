import { Component, OnInit } from '@angular/core';
import { ViewField } from 'src/app/shared/components/generic-view/generic-view.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ProgramaService } from '../programa.service';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProgramaModel } from '../models/programa.model';
import { SubProgramaModel } from './../../subPrograma/models/subPrograma.model';

@Component({
  selector: 'app-programa-detail',
  templateUrl: './programa-detail.component.html'
})
export class ProgramaDetailComponent implements OnInit {
  programa: ProgramaModel = null;
  loading = false;
  viewFields: ViewField[] = [];
  subProgramaList: SubProgramaModel[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private programaService: ProgramaService,
    private messageService: MessageService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.loadPrograma(Number(id));
      }
    });
  }

  setupViewFields(): void {
    if (!this.programa) return;

    this.viewFields = [
      {
        key: 'nombre',
        label: 'Nombre',
        value: this.programa.nombre,
        type: 'text',
        order: 1
      },
      {
        key: 'descripcion',
        label: 'Descripción',
        value: this.programa.descripcion,
        type: 'multiline',
        order: 2,
        width: 'full'
      }
    ];
  }

  loadPrograma(id: number): void {
    this.spinnerService.show();
    this.loading = true;

    this.programaService.getById(id).subscribe({
      next: (programa) => {
        this.programa = programa;
        this.setupViewFields();
        this.subProgramaList = programa.subProgramaList || [];
        this.spinnerService.hide();
        this.loading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el Programa'
        });
        this.spinnerService.hide();
        this.loading = false;
        this.programa = null;
        this.subProgramaList = [];
      }
    });
  }

  onBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}