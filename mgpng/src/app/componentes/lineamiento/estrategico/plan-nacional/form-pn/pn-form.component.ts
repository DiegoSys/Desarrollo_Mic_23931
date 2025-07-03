import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PnService } from '../pn.service';
import { OdsService } from '../../obj-des-sost/ods.service';
import { PnModel } from '../models/pn.model';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';

@Component({
  selector: 'app-pn-form',
  templateUrl: './pn-form.component.html',
})
export class PnFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  odsOptions: { label: string, value: string }[] = [];
  private initialFormValue: any;

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código PN',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [
        Validators.required, 
        Validators.maxLength(15),
        Validators.pattern('^[a-zA-Z0-9._-]+$') //Permite letras, números, puntos, guiones y guiones bajos
      ],
      disabled: this.isEditing
    },
    {
      name: 'codigoObjDessostFk',
      label: 'ODS Relacionado',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.odsOptions,
      showAddButton: true
    },
    {
      name: 'fechaInicio',
      label: 'Fecha Inicio',
      type: 'date' as const,
      required: true,
      width: 'half',
      onChange: (value: Date, formValue: PnModel) => {
        if (formValue.fechaFin && value > new Date(formValue.fechaFin)) {
          this.showWarning('La fecha de inicio no puede ser posterior a la fecha de fin');
        }
      }
    },
    {
      name: 'fechaFin',
      label: 'Fecha Fin',
      type: 'date' as const,
      required: true,
      width: 'half',
      onChange: (value: Date, formValue: PnModel) => {
        if (formValue.fechaInicio && value < new Date(formValue.fechaInicio)) {
          this.showWarning('La fecha de fin no puede ser anterior a la fecha de inicio');
        }
      }
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
    }
  ];
  

  constructor(
    private fb: FormBuilder,
    private pnService: PnService,
    private odsService: OdsService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadOdsOptions();

    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadPn(this.codigo);
      } else {
        this.loading = true;
        this.spinnerService.show();
        setTimeout(() => {
          this.loading = false;
          this.spinnerService.hide();
          this.initialFormValue = this.form.getRawValue();
        }, 0);
      }
    });

    this.form.valueChanges.subscribe(() => {});
  }

    private showWarning(message: string): void {
    this.messageService.add({
      severity: 'warn',
      summary: 'Advertencia',
      detail: message,
      life: 5000,
      sticky: false,
      closable: true
    });
  }


  loadOdsOptions(): void {
    this.odsService.getAll({ page: 0, size: 100000 }).subscribe({
      next: (response) => {
        const odsList = response.content || [];
        this.odsOptions = odsList.map((ods: PnModel) => ({
          label: (ods.codigo + ' / ' + ods.descripcion).length > 80
            ? (ods.codigo + ' / ' + ods.descripcion).substring(0, 77) + '...'
            : ods.codigo + ' / ' + ods.descripcion,
          value: ods.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoObjDessostFk') {
            return { ...field, options: this.odsOptions };
          }
          return field;
        });
  
      },
      error: (err) => {
        console.error('Error al cargar ODS:', err);
        this.showError('Error al cargar lista de ODS');
      }
    });
  }

  loadPn(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.pnService.getById(codigo).subscribe({
      next: (pn) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = pn[field.name as keyof typeof pn];

          // Convertir fechas a formato Date si vienen como string
          if ((field.name === 'fechaInicio' || field.name === 'fechaFin') && typeof defaultValue === 'string') {
            defaultValue = new Date(defaultValue);
          }

          return {
            ...field,
            defaultValue: defaultValue,
            disabled: field.name === 'codigo' ? true : field.disabled,
          };
        });

        this.loading = false;
        this.spinnerService.hide();
        setTimeout(() => {
          this.initialFormValue = this.form.getRawValue();
        }, 0);
      },
      error: (err) => {
        this.loading = false;
        this.spinnerService.hide();
        console.error('Error al cargar PN:', err);
        this.showError('Error al cargar el PN');
      }
    });
  }

  get isFormDirty(): boolean {
    if (!this.initialFormValue) return false;
    return JSON.stringify(this.initialFormValue) !== JSON.stringify(this.form.getRawValue());
  }

  onCancel(): void {
    if (this.isFormDirty) {
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
    } else {
      this.router.navigate(['../'], { relativeTo: this.route });
    }
  }

  onSubmit(formValue: PnModel): void {
    if (!formValue.descripcion || !formValue.codigoObjDessostFk ||
      !formValue.fechaInicio || !formValue.fechaFin) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }

    if (new Date(formValue.fechaFin) < new Date(formValue.fechaInicio)) {
      this.showError('La fecha de fin no puede ser anterior a la fecha de inicio');
      return;
    }

    const pnData: PnModel = {
      codigo: this.isEditing ? this.codigo : formValue.codigo,
      codigoObjDessostFk: formValue.codigoObjDessostFk,
      descripcion: formValue.descripcion,
      estado: 'A',
      fechaInicio: new Date(formValue.fechaInicio),
      fechaFin: new Date(formValue.fechaFin)
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este PN?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.pnService.update(this.codigo, pnData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'PN actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el PN: ${err.message}`);
            }
          });
        } else {
          this.pnService.create(pnData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'PN creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el PN: ${err.message}`);
            }
          });
        }
      },
      reject: () => {
        this.spinnerService.hide();
      }
    });
  }

  onAddOption(field: FormField) {
    if (field.name === 'codigoObjDessostFk') {
      this.router.navigate(['/lineamiento/obj-des-sost/new']);
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