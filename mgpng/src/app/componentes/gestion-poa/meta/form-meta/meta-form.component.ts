import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MetaService } from '../meta.service';
import { OpnService } from '../../opn/opn.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { MetaUpdateModel, MetaModel } from '../models/meta.model';

@Component({
  selector: 'app-meta-form',
  templateUrl: './meta-form.component.html',
})
export class MetaFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  pdnOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Meta',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(10)],
      disabled: this.isEditing
    },
    {
      name: 'codigoOpnFk',
      label: 'Objetivo Nacional Relacionado',
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
    private metaService: MetaService,
    private opnService: OpnService,
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
        this.loadMeta(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadPdnOptions(): void {
    this.opnService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const pdnList = response.content || [];
        this.pdnOptions = pdnList.map((pdn: any) => ({
          label: pdn.codigo + ' / ' + pdn.descripcion,
          value: pdn.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoOpnFk') {
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

  loadMeta(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.metaService.getById(codigo).subscribe({
      next: (meta) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = meta[field.name as keyof typeof meta];
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
        console.error('Error al cargar Meta:', err);
        this.showError('Error al cargar la Meta');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: MetaUpdateModel): void {
    if (!formValue.descripcion || !formValue.codigoOpnFk ) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const metaData: MetaUpdateModel = {
      codigoOpnFk: formValue.codigoOpnFk,
      descripcion: formValue.descripcion,
      estado: 'A',
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} esta Meta?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.metaService.update(this.codigo, metaData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Meta actualizada correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar la Meta: ${err.message}`);
            }
          });
        } else {
          const createData: MetaModel = {
            ...metaData,
            codigo: formValue.codigo,
            estado: 'A'
          };
          this.metaService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Meta creada correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear la Meta: ${err.message}`);
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