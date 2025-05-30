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
  odsOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código PN',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: this.isEditing
    },
    {
      name: 'codigoObjDessostFk',
      label: 'ODS Relacionado',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.odsOptions
    },
    /*{
      name: 'estado',
      label: 'Estado',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: [
        { label: 'Activo', value: 'A' },
        { label: 'Inactivo', value: 'I' }
      ]
    },*/
    {
      name: 'fechaInicio',
      label: 'Fecha Inicio',
      type: 'date' as const,
      required: true,
      width: 'half',
      onChange: (value: Date, formValue: PnModel) => {
        if (formValue.fechaFin && value > new Date(formValue.fechaFin)) {
          this.showError('La fecha de inicio no puede ser posterior a la fecha de fin');
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
          this.showError('La fecha de fin no puede ser anterior a la fecha de inicio');
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
        this.spinnerService.hide();
      }
    });
  }

  loadOdsOptions(): void {
    this.odsService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const odsList = response.content || [];
        this.odsOptions = odsList.map((ods: PnModel) => ({
          label: ods.codigo + ' / ' + ods.descripcion,
          value: ods.codigo
        }));
        console.log('ODS Options:', this.odsOptions);
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoObjDessostFk') {
            return {...field, options: this.odsOptions};
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
            disabled: field.name === 'codigo' ? true : field.disabled
          };
        });

        console.log('Form fields after load:', this.formFields);
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        console.error('Error al cargar PN:', err);
        this.showError('Error al cargar el PN');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: PnModel): void {
    console.log('Form values:', formValue); // Debug log
    
    if (!formValue.descripcion || !formValue.codigoObjDessostFk || 
        !formValue.fechaInicio || !formValue.fechaFin) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }

    // Validar que fechaFin no sea anterior a fechaInicio
    if (new Date(formValue.fechaFin) < new Date(formValue.fechaInicio)) {
      this.showError('La fecha de fin no puede ser anterior a la fecha de inicio');
      return;
    }
    
    const pnData: PnModel = {
      codigo: this.isEditing ? this.codigo : formValue.codigo,
      codigoObjDessostFk: formValue.codigoObjDessostFk,
      descripcion: formValue.descripcion,
      estado: 'A', // Default active status
      fechaInicio: new Date(formValue.fechaInicio),
      fechaFin: new Date(formValue.fechaFin)
    };

    console.log('Sending data:', pnData); // Debug log

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
      life: 5000,  // Show for 5 seconds
      sticky: false,
      closable: true
    });
  }
}
