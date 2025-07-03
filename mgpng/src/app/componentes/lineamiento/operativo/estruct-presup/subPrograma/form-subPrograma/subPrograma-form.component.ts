import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormField } from '../../../../../../shared/components/generic-form/generic-form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { SubProgramaService } from '../subPrograma.service';
import { ProgramaService } from '../../../estruct-presup/programa/programa.service';
import { ProyectoService } from '../../proyecto/proyecto.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { SubProgramaModel, SubProgramaUpdateModel } from '../models/subPrograma.model';

@Component({
  selector: 'app-subprograma-form',
  templateUrl: './subPrograma-form.component.html',
})
export class SubProgramaFormComponent implements OnInit {
  @Input() codigoPrograma?: number; // Cambiado a number para id

  idSubPrograma?: number;
  form: FormGroup;
  isEditing = false;
  loading = false;

  programaOptions: any[] = [];
  proyectoOptions: any[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: false // Se ajusta dinámicamente
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
      name: 'programaId',
      label: 'Programa',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.programaOptions,
      optionLabel: 'nombre',
      optionValue: 'id',
      validators: [Validators.required],
      disabled: true // Siempre deshabilitado
    }
  ];

  constructor(
    private fb: FormBuilder,
    private subProgramaService: SubProgramaService,
    private programaService: ProgramaService,
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
      const id = params['id'];
      this.isEditing = !!id;
      this.codigoPrograma = params['codigoPrograma'] ? Number(params['codigoPrograma']) : this.codigoPrograma;

      if (!this.isEditing && this.codigoPrograma) {
        this.formFields = this.formFields.map(field => {
          if (field.name === 'programaId') {
            return { ...field, defaultValue: this.codigoPrograma, disabled: true };
          }
          if (field.name === 'codigo') {
            return { ...field, defaultValue: '', disabled: false };
          }
          return field;
        });
      }

      if (this.isEditing) {
        this.loadSubPrograma(Number(id));
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadOptions(): void {
    this.spinnerService.show();

    this.programaService.getAll({ page: 0, size: 100000 }).subscribe({
      next: (response) => {
        this.programaOptions = response.content || [];
        this.formFields = this.formFields.map(field => {
          if (field.name === 'programaId') {
            return { ...field, options: this.programaOptions };
          }
          return field;
        });
      },
      error: () => {
        this.showError('Error al cargar los programas');
      }
    });
  }

  loadSubPrograma(id: number): void {
    this.loading = true;
    this.spinnerService.show();
    this.subProgramaService.getById(id).subscribe({
      next: (subPrograma) => {
        this.idSubPrograma = subPrograma.id;
        this.formFields = this.formFields.map(field => {
          let defaultValue = subPrograma[field.name as keyof typeof subPrograma];
          if (field.name === 'proyectos' && Array.isArray(defaultValue)) {
            defaultValue = defaultValue.map((p: any) => typeof p === 'string' ? p : p.codigo);
          }
          // Siempre deshabilitado para código y programa
          if (field.name === 'codigo' || field.name === 'programaId') {
            return {
              ...field,
              defaultValue: defaultValue,
              disabled: true
            };
          }
          return {
            ...field,
            defaultValue: defaultValue,
            disabled: field.disabled
          };
        });
        this.loading = false;
        this.spinnerService.hide();
      },
      error: () => {
        this.showError('Error al cargar el SubPrograma');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: SubProgramaUpdateModel): void {
    if (!formValue.nombre || !formValue.descripcion || !formValue.programaId) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }

    const proyectos = (formValue.proyectos || []).map((p: any) => typeof p === 'string' ? p : p.codigo);

    const subProgramaData: SubProgramaUpdateModel = {
      ...formValue,
      proyectos: proyectos
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este SubPrograma?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing && this.idSubPrograma) {
          this.subProgramaService.update(this.idSubPrograma, subProgramaData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'SubPrograma actualizado correctamente'
              });
              const programaId = this.route.snapshot.queryParamMap.get('codigoPrograma');
              this.router.navigate(['/lineamiento/operativo/estruct-presup/programa/view'], {
                queryParams: { id: programaId }
              });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el SubPrograma: ${err.message}`);
            }
          });
        } else {
          const createData: SubProgramaModel = {
            ...subProgramaData,
            proyectos: proyectos
          };
          this.subProgramaService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'SubPrograma creado correctamente'
              });
              const programaId = this.route.snapshot.queryParamMap.get('codigoPrograma');
              this.router.navigate(['/lineamiento/operativo/estruct-presup/programa/view'], {
                queryParams: { id: programaId }
              });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el SubPrograma: ${err.message}`);
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
    const programaId = this.route.snapshot.queryParamMap.get('codigoPrograma');
    this.router.navigate(['/lineamiento/operativo/estruct-presup/programa/view'], {
      queryParams: { id: programaId }
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