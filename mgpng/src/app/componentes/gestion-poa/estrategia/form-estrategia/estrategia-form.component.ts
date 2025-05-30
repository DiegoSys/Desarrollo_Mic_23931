import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EstrategiaService } from '../estrategia.service';
import { PdnService } from '../../pdn/pdn.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { EstrategiaUpdateModel, EstrategiaModel } from '../models/estrategia.model';

@Component({
  selector: 'app-estrategia-form',
  templateUrl: './estrategia-form.component.html',
})
export class EstrategiaFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  pdnOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Estrategia',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: this.isEditing
    },
    {
      name: 'codigoPdnFk',
      label: 'Política PDN Relacionada',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.pdnOptions
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
    private estrategiaService: EstrategiaService,
    private pdnService: PdnService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadPdnOptions();
    
    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadEstrategia(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadPdnOptions(): void {
    this.pdnService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const pdnList = response.content || [];
        this.pdnOptions = pdnList.map((pdn: any) => ({
          label: pdn.codigo + ' / ' + pdn.descripcion,
          value: pdn.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoPdnFk') {
            return {...field, options: this.pdnOptions};
          }
          return field;
        });
      },
      error: (err) => {
        console.error('Error al cargar Políticas PDN:', err);
        this.showError('Error al cargar lista de Políticas del PDN');
      }
    });
  }

  loadEstrategia(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.estrategiaService.getById(codigo).subscribe({
      next: (estrategia) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = estrategia[field.name as keyof typeof estrategia];
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
        console.error('Error al cargar Estrategia:', err);
        this.showError('Error al cargar la Estrategia');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: EstrategiaUpdateModel): void {
    if (!formValue.descripcion || !formValue.codigoPdnFk) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const estrategiaData: EstrategiaUpdateModel = {
      codigoPdnFk: formValue.codigoPdnFk,
      descripcion: formValue.descripcion,
      estado: 'A',
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} esta Estrategia?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.estrategiaService.update(this.codigo, estrategiaData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Estrategia actualizada correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar la Estrategia: ${err.message}`);
            }
          });
        } else {
          const createData: EstrategiaModel = {
            ...estrategiaData,
            codigo: formValue.codigo,
            estado: 'A'
          };
          this.estrategiaService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Estrategia creada correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear la Estrategia: ${err.message}`);
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