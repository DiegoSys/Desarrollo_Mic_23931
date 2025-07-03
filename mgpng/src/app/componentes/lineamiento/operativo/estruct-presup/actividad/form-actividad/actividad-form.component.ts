import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormField } from '../../../../../../shared/components/generic-form/generic-form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ProyectoService } from '../../proyecto/proyecto.service';
import { ActividadService } from '../actividad.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ActividadModel, ActividadUpdateModel } from '../models/actividad.model';

@Component({
  selector: 'app-actividad-form',
  templateUrl: './actividad-form.component.html',
})
export class ActividadFormComponent implements OnInit {
  programaId?: number;
  subProgramaId?: number;
  proyectoId?: number;
  actividadId?: number;
  form: FormGroup;
  isEditing = false;
  loading = false;

  proyectoOptions: any[] = [];

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
        Validators.minLength(10),
        Validators.maxLength(500)
      ]
    },
    {
      name: 'proyectoId',
      label: 'Proyecto',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.proyectoOptions,
      optionLabel: 'nombre',
      optionValue: 'id',
      validators: [Validators.required],
      disabled: false
    }
  ];

  constructor(
    private fb: FormBuilder,
    private proyectoService: ProyectoService,
    private actividadService: ActividadService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      // Si viene 'actividadId', es edición. Si no, revisa 'id'
      if (params['actividadId']) {
        this.actividadId = Number(params['actividadId']);
        this.isEditing = true;
        this.proyectoId = params['proyectoId'] ? Number(params['proyectoId']) : undefined;
      } else if (params['id'] && params['proyectoId']) {
        // Si vienen ambos, es edición (id=actividad, proyectoId=proyecto)
        this.actividadId = Number(params['id']);
        this.proyectoId = Number(params['proyectoId']);
        this.isEditing = true;
      } else if (params['id']) {
        // Si solo viene 'id', es creación (id=proyecto)
        this.proyectoId = Number(params['id']);
        this.actividadId = undefined;
        this.isEditing = false;
      } else {
        this.actividadId = undefined;
        this.proyectoId = undefined;
        this.isEditing = false;
      }
  
      this.programaId = params['programaId'] ? Number(params['programaId']) : undefined;
      this.subProgramaId = params['subProgramaId'] ? Number(params['subProgramaId']) : undefined;
  
      this.loadOptions();
    });
  }
  
  loadOptions(): void {
    if (this.programaId === undefined || this.subProgramaId === undefined) {
      this.showError('No se puede cargar la lista de proyectos: faltan programaId o subProgramaId');
      return;
    }
  
    this.spinnerService.show();
  
    this.proyectoService.getByProgramaAndSubprogramaPaginated(
      this.programaId,
      this.subProgramaId,
      { page: 0, size: 100000 }
    ).subscribe({
      next: (response) => {
        this.proyectoOptions = response.content || [];
        this.formFields = this.formFields.map(field => {
          if (field.name === 'proyectoId') {
            return { ...field, options: this.proyectoOptions };
          }
          return field;
        });
  
        // Si NO está editando, setea el valor por defecto del campo 'proyectoId'
        if (!this.isEditing && this.proyectoId !== undefined) {
          this.formFields = this.formFields.map(field => {
            if (field.name === 'proyectoId') {
              return { ...field, defaultValue: this.proyectoId, disabled: true };
            }
            if (field.name === 'codigo') {
              return { ...field, defaultValue: '', disabled: false };
            }
            return field;
          });
          this.spinnerService.hide(); // Oculta el spinner en modo creación
        }
  
        // Si está editando, carga la actividad y deshabilita código y proyectoId
        if (this.isEditing && this.actividadId !== undefined && this.actividadId !== null && this.actividadId > 0) {
          this.loadActividad(this.actividadId);
        }
      },
      error: () => {
        this.showError('Error al cargar los proyectos');
        this.spinnerService.hide();
      }
    });
  }


  loadActividad(id: number): void {
    this.loading = true;
    this.spinnerService.show();
    this.actividadService.getById(id).subscribe({
      next: (actividad: ActividadModel) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = actividad[field.name as keyof typeof actividad];
          if (field.name === 'codigo' || field.name === 'proyectoId') {
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
        this.showError('Error al cargar la Actividad');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: ActividadUpdateModel): void {
    if (!formValue.nombre || !formValue.descripcion || !formValue.proyectoId) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }

    const actividadData: ActividadUpdateModel = {
      ...formValue,
      proyectoId: this.proyectoId
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} esta Actividad?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing && this.actividadId !== undefined && this.proyectoId !== undefined) {
          this.actividadService.update(
            this.actividadId,
            this.proyectoId,
            actividadData,
          ).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Actividad actualizada correctamente'
              });
              this.router.navigate(['/lineamiento/operativo/estruct-presup/proyecto/view'], {
                queryParams: {
                  id: this.proyectoId,
                  programaId: this.programaId,
                  subProgramaId: this.subProgramaId
                }
              });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar la Actividad: ${err.message}`);
            }
          });
        } else if (this.proyectoId !== undefined) {
          const createData: ActividadModel = {
            ...actividadData,
            proyectoId: this.proyectoId
          };
          this.actividadService.create(
            createData,
            this.proyectoId,
          ).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Actividad creada correctamente'
              });
              this.router.navigate(['/lineamiento/operativo/estruct-presup/proyecto/view'], {
                queryParams: {
                  id: this.proyectoId,
                  programaId: this.programaId,
                  subProgramaId: this.subProgramaId
                }
              });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear la Actividad: ${err.message}`);
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
    this.router.navigate(['/lineamiento/operativo/estruct-presup/proyecto/view'], {
      queryParams: {
        id: this.proyectoId,
        programaId: this.programaId,
        subProgramaId: this.subProgramaId
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