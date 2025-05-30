import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProInsService } from '../prog-ins.service';
import { MetaService } from '../../meta/meta.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProInsModel, ProInsUpdateModel } from '../models/prog-ins.model';

@Component({
  selector: 'app-prog-ins-form',
  templateUrl: './prog-ins-form.component.html',
})
export class ProgInsFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  metaOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Programa Institucional',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: this.isEditing
    },
    {
      name: 'codigoMetaFk',
      label: 'Meta Relacionada',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.metaOptions
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
    private progInsService: ProInsService,
    private metaService: MetaService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadMetaOptions();
    
    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadProgIns(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadMetaOptions(): void {
    this.metaService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const metaList = response.content || [];
        this.metaOptions = metaList.map((meta: any) => ({
          label: meta.codigo + ' / ' + meta.descripcion,
          value: meta.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoMetaFk') {
            return {...field, options: this.metaOptions};
          }
          return field;
        });
      },
      error: (err) => {
        console.error('Error al cargar Metas:', err);
        this.showError('Error al cargar lista de Metas');
      }
    });
  }

  loadProgIns(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.progInsService.getById(codigo).subscribe({
      next: (progIns) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = progIns[field.name as keyof typeof progIns];
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
        console.error('Error al cargar Programa Institucional:', err);
        this.showError('Error al cargar el Programa Institucional');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: ProInsUpdateModel): void {
    if (!formValue.descripcion || !formValue.codigoMetaFk ) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const progInsData: ProInsUpdateModel = {
      codigoMetaFk: formValue.codigoMetaFk,
      descripcion: formValue.descripcion,
      estado: 'A'
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Programa Institucional?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.progInsService.update(this.codigo, progInsData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Programa Institucional actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Programa Institucional: ${err.message}`);
            }
          });
        } else {
          const createData: ProInsModel = {
            ...progInsData,
            codigo: formValue.codigo,
            estado: 'A'
          };
          this.progInsService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Programa Institucional creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Programa Institucional: ${err.message}`);
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