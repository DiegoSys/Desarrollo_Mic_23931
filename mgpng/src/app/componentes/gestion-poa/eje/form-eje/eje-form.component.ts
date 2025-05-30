import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EjeService } from '../eje.service';
import { PnService } from '../../plan-nacional/pn.service';
import { EjeModel, EjeUpdateModel } from '../models/eje.model';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';

@Component({
  selector: 'app-eje-form',
  templateUrl: './eje-form.component.html',
})
export class EjeFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  pnOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Eje',
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
    private ejeService: EjeService,
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
        this.loadEje(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadPnOptions(): void {
    this.pnService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const pnList = response.content || [];
        this.pnOptions = pnList.map((pn: EjeModel) => ({
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

  loadEje(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.ejeService.getById(codigo).subscribe({
      next: (eje) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = eje[field.name as keyof typeof eje];
          
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
        console.error('Error al cargar Eje:', err);
        this.showError('Error al cargar el Eje');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: EjeModel): void {
    if (!formValue.descripcion || !formValue.codigoPlanNacionalFk ) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const ejeData: EjeUpdateModel = {
      codigoPlanNacionalFk: formValue.codigoPlanNacionalFk,
      descripcion: formValue.descripcion,
      estado: 'A', // Default active status
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Eje?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.ejeService.update(this.codigo, ejeData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Eje actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Eje: ${err.message}`);
            }
          });
        } else {
          const createData: EjeModel = {
            ...ejeData,
            codigo: formValue.codigo
          };
          this.ejeService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Eje creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Eje: ${err.message}`);
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
