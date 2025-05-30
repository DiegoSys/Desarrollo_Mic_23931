import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormField } from '../../../../shared/components/generic-form/generic-form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { GrupoService } from '../grupo.service';
import { NaturalezaService } from '../../naturaleza/naturaleza.service';
import { SubGrupoService } from '../../subGrupo/subGrupo.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { GrupoModel, GrupoUpdateModel } from '../models/grupo.model';

@Component({
  selector: 'app-grupo-form',
  templateUrl: './grupo-form.component.html',
})
export class GrupoFormComponent implements OnInit {
  @Input() codigoNaturaleza?: string;

  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;

  naturalezaOptions: any[] = [];
  subGrupoOptions: any[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: this.isEditing
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
      name: 'codigoNaturalezaFk',
      label: 'Naturaleza',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.naturalezaOptions,
      optionLabel: 'nombre',
      optionValue: 'codigo',
      validators: [Validators.required],
      disabled: !!this.codigoNaturaleza
    },
    {
      name: 'presSubGrupoList',
      label: 'Sub Grupos',
      type: 'multiselect' as const,
      required: false,
      width: 'full',
      options: this.subGrupoOptions,
      optionLabel: 'nombre',
      optionValue: 'codigo'
    }
  ];

  constructor(
    private fb: FormBuilder,
    private grupoService: GrupoService,
    private naturalezaService: NaturalezaService,
    private subGrupoService: SubGrupoService,
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
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadGrupo(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadOptions(): void {
    this.spinnerService.show();

    this.naturalezaService.getAll().subscribe({
      next: (response) => {
        this.naturalezaOptions = response.content || [];
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoNaturalezaFk') {
            return { ...field, options: this.naturalezaOptions };
          }
          return field;
        });
      },
      error: () => {
        this.showError('Error al cargar las naturalezas');
      }
    });

    this.subGrupoService.getAll().subscribe({
      next: (response) => {
        this.subGrupoOptions = response.content || [];
        this.formFields = this.formFields.map(field => {
          if (field.name === 'presSubGrupoList') {
            return { ...field, options: this.subGrupoOptions };
          }
          return field;
        });
        this.spinnerService.hide();
      },
      error: () => {
        this.showError('Error al cargar los subgrupos');
        this.spinnerService.hide();
      }
    });
  }

  loadGrupo(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.grupoService.getById(codigo).subscribe({
      next: (grupo) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = grupo[field.name as keyof typeof grupo];
          return {
            ...field,
            defaultValue: defaultValue,
            disabled: field.name === 'codigo' ? true : field.disabled
          };
        });
        this.loading = false;
        this.spinnerService.hide();
      },
      error: () => {
        this.showError('Error al cargar el Grupo');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: GrupoUpdateModel): void {
    if (!formValue.nombre || !formValue.descripcion || !formValue.codigo || !formValue.codigoNaturalezaFk) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
  
    // Concatenar el código de naturaleza y el ingresado
    const codigoNaturaleza = this.codigoNaturaleza || formValue.codigoNaturalezaFk;
    const codigoGrupo = `${codigoNaturaleza}${formValue.codigo}`;
  
    const grupoData: GrupoUpdateModel = {
      ...formValue,
      codigo: codigoGrupo,
      codigoNaturalezaFk: codigoNaturaleza,
      presSubGrupoList: formValue.presSubGrupoList || []
    };
  
    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Grupo?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.grupoService.update(this.codigo, grupoData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Grupo actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Grupo: ${err.message}`);
            }
          });
        } else {
          const createData: GrupoModel = {
            ...grupoData,
            codigo: codigoGrupo,
            presSubGrupoList: formValue.presSubGrupoList ?? []
          };
          this.grupoService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Grupo creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Grupo: ${err.message}`);
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
    this.confirmationService.confirm({
      message: '¿Está seguro de que desea cancelar? Los cambios no guardados se perderán.',
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.router.navigate(['../'], { relativeTo: this.route });
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