import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProgramaService } from '../programa.service';
import { SubProgramaService } from './../../subPrograma/subPrograma.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProgramaModel, ProgramaUpdateModel } from '../models/programa.model';

@Component({
  selector: 'app-programa-form',
  templateUrl: './programa-form.component.html',
})
export class ProgramaFormComponent implements OnInit {
  idPrograma?: number;
  form: FormGroup;
  isEditing = false;
  loading = false;
  subProgramaOptions: { label: string, value: string }[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Programa',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: false // Siempre false, se deshabilita dinámicamente en edición
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
    }
  ];

  constructor(
    private fb: FormBuilder,
    private programaService: ProgramaService,
    private subProgramaService: SubProgramaService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadSubProgramaOptions();

    this.route.queryParams.subscribe(params => {
      const id = params['id'];
      this.isEditing = !!id;
      if (this.isEditing) {
        this.loadPrograma(Number(id));
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadSubProgramaOptions(): void {
    this.subProgramaService.getAll({ page: 0, size: 100000 }).subscribe({
      next: (response) => {
        const subProgramaList = response.content || [];
        this.subProgramaOptions = subProgramaList.map((sp: any) => ({
          label: sp.codigo + ' / ' + sp.nombre,
          value: sp.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'subProgramaList') {
            return { ...field, options: this.subProgramaOptions };
          }
          return field;
        });
      },
      error: (err) => {
        this.showError('Error al cargar lista de Subprogramas');
      }
    });
  }

  loadPrograma(id: number): void {
    this.loading = true;
    this.spinnerService.show();
    this.programaService.getById(id).subscribe({
      next: (programa) => {
        this.idPrograma = programa.id; // Guardar el id para update
        this.formFields = this.formFields.map(field => {
          let defaultValue = programa[field.name as keyof typeof programa];
          if (field.name === 'subProgramaList' && Array.isArray(defaultValue)) {
            defaultValue = defaultValue.map((sp: any) => typeof sp === 'string' ? sp : sp.codigo);
          }
          // Deshabilita el campo código solo en edición
          return {
            ...field,
            defaultValue: defaultValue,
            disabled: field.name === 'codigo' ? true : field.disabled
          };
        });
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        this.showError('Error al cargar el Programa');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: ProgramaUpdateModel): void {
    if (!formValue.nombre || !formValue.descripcion) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }

    // Solo los códigos de subprograma
    const subProgramaList = (formValue.subProgramaList || []).map((sp: any) => typeof sp === 'string' ? sp : sp.codigo);

    const programaData: ProgramaUpdateModel = {
      codigo: formValue.codigo,
      nombre: formValue.nombre,
      descripcion: formValue.descripcion,
      subProgramaList: subProgramaList
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Programa?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing && this.idPrograma) {
          this.programaService.update(this.idPrograma, programaData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Programa actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Programa: ${err.message}`);
            }
          });
        } else {
          const createData: ProgramaModel = {
            ...programaData,
            codigo: (formValue as any).codigo,
            estado: 'A',
            subProgramaList: subProgramaList
          };
          this.programaService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Programa creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Programa: ${err.message}`);
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
    this.router.navigate(['../'], { relativeTo: this.route });
  }

  onAddOption(field: FormField) {
    if (field.name === 'subProgramaList') {
      this.router.navigate(['/lineamiento/subPrograma/new']);
    }
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