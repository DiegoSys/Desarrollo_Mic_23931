import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormField } from '../../../../../../shared/components/generic-form/generic-form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { SubProgramaService } from '../../subPrograma/subPrograma.service';
import { ActividadService } from '../../actividad/actividad.service';
import { ProyectoService } from '../proyecto.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProyectoModel, ProyectoUpdateModel } from '../models/proyecto.model';

@Component({
  selector: 'app-proyecto-form',
  templateUrl: './proyecto-form.component.html',
})
export class ProyectoFormComponent implements OnInit {
  programaId?: number;
  subProgramaId?: number;
  proyectoId?: number;
  form: FormGroup;
  isEditing = false;
  loading = false;

  subProgramaOptions: any[] = [];
  actividadOptions: any[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: false
    },
    {
      name: 'nombre',
      label: 'Nombre',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(100)]
    },
    {
      name: 'descripcion',
      label: 'Descripción',
      type: 'textarea' as const,
      required: true,
      width: 'full',
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(500)
      ]
    },
    {
      name: 'subProgramaId',
      label: 'Subprograma',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.subProgramaOptions,
      optionLabel: 'nombre',
      optionValue: 'id',
      validators: [Validators.required],
      disabled: false
    }
  ];

  constructor(
    private fb: FormBuilder,
    private subProgramaService: SubProgramaService,
    private proyectoService: ProyectoService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadOptions();

    this.route.queryParams.subscribe(params => {
      this.proyectoId = params['id'] ? Number(params['id']) : undefined;
      this.isEditing = !!this.proyectoId;
      // Asegura que los parámetros sean numéricos
      this.programaId = params['programaId'] ? Number(params['programaId']) : undefined;
      this.subProgramaId = params['subProgramaId'] ? Number(params['subProgramaId']) : undefined;

      // Si NO está editando, setea el valor por defecto del campo 'subProgramaId'
      if (!this.isEditing && this.subProgramaId !== undefined) {
        this.formFields = this.formFields.map(field => {
          if (field.name === 'subProgramaId') {
            return { ...field, defaultValue: this.subProgramaId, disabled: true };
          }
          if (field.name === 'codigo') {
            return { ...field, defaultValue: '', disabled: false };
          }
          return field;
        });
      }

      if (this.isEditing && this.proyectoId !== undefined) {
        this.loadProyecto(this.proyectoId);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadOptions(): void {
    this.spinnerService.show();

    this.subProgramaService.getAll({ page: 0, size: 100000 }).subscribe({
      next: (response) => {
        this.subProgramaOptions = response.content || [];
        this.formFields = this.formFields.map(field => {
          if (field.name === 'subProgramaId') {
            return { ...field, options: this.subProgramaOptions };
          }
          return field;
        });
        this.spinnerService.hide();
      },
      error: () => {
        this.showError('Error al cargar los subprogramas');
        this.spinnerService.hide();
      }
    });
  }

  loadProyecto(id: number): void {
    this.loading = true;
    this.spinnerService.show();
    this.proyectoService.getById(id).subscribe({
      next: (proyecto: ProyectoModel) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = proyecto[field.name as keyof typeof proyecto];
          if (field.name === 'actividades' && Array.isArray(defaultValue)) {
            defaultValue = defaultValue.map((a: any) => typeof a === 'string' ? a : a.codigo);
          }
          // Deshabilita código y subprograma al editar
          if (field.name === 'codigo' || field.name === 'subProgramaId') {
            return {
              ...field,
              defaultValue: defaultValue,
              disabled: true
            };
          }
          return {
            ...field,
            defaultValue: defaultValue
          };
        });
        this.loading = false;
        this.spinnerService.hide();
      },
      error: () => {
        this.showError('Error al cargar el Proyecto');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: ProyectoUpdateModel): void {
    if (!formValue.nombre || !formValue.descripcion || !formValue.subProgramaId) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }

    const actividades = (formValue.actividades || []).map((a: any) => typeof a === 'string' ? a : a.codigo);

    const proyectoData: ProyectoUpdateModel = {
      ...formValue,
      actividades: actividades
    };


    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Proyecto?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing && this.proyectoId !== undefined) {
          this.proyectoService.update(this.proyectoId, proyectoData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Proyecto actualizado correctamente'
              });
              this.router.navigate(['/lineamiento/operativo/estruct-presup/subPrograma/view'], {
                queryParams: {
                  id: this.subProgramaId,
                  codigoPrograma: this.programaId
                }
              });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Proyecto: ${err.message}`);
            }
          });
        } else if (this.programaId !== undefined && this.subProgramaId !== undefined) {
          const createData: ProyectoModel = {
            ...proyectoData,
            actividades: actividades
          };
          this.proyectoService.create(createData, this.programaId, this.subProgramaId).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Proyecto creado correctamente'
              });
              this.router.navigate(['/lineamiento/operativo/estruct-presup/subPrograma/view'], {
                queryParams: {
                  id: this.subProgramaId,
                  codigoPrograma: this.programaId
                }
              });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Proyecto: ${err.message}`);
            }
          });
        }
      },
      reject: () => {
        this.spinnerService.hide();
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/lineamiento/operativo/estruct-presup/subPrograma/view'], {
      queryParams: {
        id: this.subProgramaId,
        codigoPrograma: this.programaId
      }
    });
  }

  private showError(message: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message,
      life: 5000,
      sticky: false,
      closable: true
    });
  }
}