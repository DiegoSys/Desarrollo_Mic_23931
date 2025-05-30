import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { OdsService } from '../ods.service';
import { OdsModel } from '../models/obj-des-sost.model';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';


@Component({
  selector: 'app-obj-des-sost-form',
  templateUrl: './obj-des-sost-form.component.html',
})
export class ObjDesSostFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código ODS',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: this.isEditing
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
    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadOds(this.codigo);
      } else {
        this.spinnerService.hide(); // Asegurar que el spinner esté oculto al inicio
      }
    });
  }

  loadOds(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.odsService.getById(codigo).subscribe({
      next: (ods) => {
        this.formFields = this.formFields.map(field => {
          return {
            ...field,
            defaultValue: ods[field.name as keyof typeof ods],
            disabled: field.name === 'codigo' ? true : field.disabled
          };
        });
        this.loading = false;
        this.spinnerService.hide();
      },
      error: (err) => {
        console.error('Error al cargar ODS:', err);
        this.showError('Error al cargar el ODS');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: any): void {
      console.log('Received form values:', formValue);
      
      if (!formValue.descripcion) {
        console.error('Missing required fields:', formValue);
        this.showError('Faltan datos requeridos en el formulario');
        return;
      }
      
      const odsData: OdsModel = {
        codigo: this.isEditing ? this.codigo : formValue.codigo,
        descripcion: formValue.descripcion,
        estado: 'A'
      };
      console.log('Data to send:', odsData); // Debug log
    
      this.confirmationService.confirm({
        message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este ODS?`,
        header: 'Confirmación',
        icon: 'pi pi-question-circle',
        accept: () => {
          this.spinnerService.show();
      if (this.isEditing) {
            this.odsService.update(this.codigo, {
              descripcion: odsData.descripcion,
              estado: 'A'
            }).subscribe({
              next: () => {
                this.messageService.add({
                  severity: 'success',
                  summary: 'Éxito',
                  detail: 'ODS actualizado correctamente'
                });
                this.router.navigate(['../'], { relativeTo: this.route });
              },
              error: (err) => {
                this.spinnerService.hide();
                this.showError(`Error al actualizar el ODS: ${err.message}`);
              }
            });
          } else {
            this.odsService.create(odsData).subscribe({
              next: () => {
                this.messageService.add({
                  severity: 'success',
                  summary: 'Éxito',
                  detail: 'ODS creado correctamente'
                });
                this.router.navigate(['../'], { relativeTo: this.route });
              },
              error: (err) => {
                this.spinnerService.hide();
                this.showError(`Error al crear el ODS: ${err.message}`);
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
    console.log('Cancelando...');
    this.confirmationService.confirm({
      message: '¿Está seguro de que desea cancelar? Los cambios no guardados se perderán.',
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.router.navigate(['../'], { relativeTo: this.route });
      },
      reject: () => {
        // No hacer nada si el usuario rechaza
      }
    });
  }

    getErrorMessage(controlName: string): string {
    const control = this.form.get(controlName);
  
    if (control?.hasError('required')) {
      return 'Este campo es obligatorio.';
    }
    if (control?.hasError('maxlength')) {
      return `El campo no puede tener más de ${control.errors?.['maxlength'].requiredLength} caracteres.`;
    }
    if (control?.hasError('minlength')) {
      return `El campo debe tener al menos ${control.errors?.['minlength'].requiredLength} caracteres.`;
    }
  
    return 'Campo inválido.';
  }

  private showError(message: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message
    });
  }
}
