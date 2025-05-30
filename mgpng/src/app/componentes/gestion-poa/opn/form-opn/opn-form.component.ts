import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { OpnService } from '../opn.service';
import { PnService } from '../../plan-nacional/pn.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { OpnModel, OpnUpdateModel } from '../models/opn.model';

@Component({
  selector: 'app-opn-form',
  templateUrl: './opn-form.component.html',
})
export class OpnFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  pnOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Objetivo Nacional',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: this.isEditing
    },
    {
      name: 'codigoPlanNacionalFk',
      label: 'Plan Nacional Relacionado',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.pnOptions
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
    private opnService: OpnService,
    private pnService: PnService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadPnOptions();
    
    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadOpn(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadPnOptions(): void {
    this.pnService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const pnList = response.content || [];
        this.pnOptions = pnList.map((pn: OpnModel) => ({
          label: pn.codigo + ' / ' + pn.descripcion,
          value: pn.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoPlanNacionalFk') {
            return {...field, options: this.pnOptions};
          }
          return field;
        });
      },
      error: (err) => {
        console.error('Error al cargar Planes Nacionales:', err);
        this.showError('Error al cargar lista de Planes Nacionales');
      }
    });
  }

  loadOpn(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.opnService.getById(codigo).subscribe({
      next: (opn) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = opn[field.name as keyof typeof opn];
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
        console.error('Error al cargar Objetivo Nacional:', err);
        this.showError('Error al cargar el Objetivo Nacional');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: OpnModel): void {
    if (!formValue.descripcion || !formValue.codigoPlanNacionalFk ) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const opnData: OpnUpdateModel = {
      codigoPlanNacionalFk: formValue.codigoPlanNacionalFk,
      descripcion: formValue.descripcion,
      estado: 'A', // Default active status
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Objetivo Nacional?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.opnService.update(this.codigo, opnData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Objetivo Nacional actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Objetivo Nacional: ${err.message}`);
            }
          });
        } else {
          const createData: OpnModel = {
            ...opnData,
            codigo: formValue.codigo
          };
          this.opnService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Objetivo Nacional creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Objetivo Nacional: ${err.message}`);
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