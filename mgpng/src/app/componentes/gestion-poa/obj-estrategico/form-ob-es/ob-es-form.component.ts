import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ObEstraService } from '../ob-es.service';
import { MetaService } from '../../meta/meta.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ObEstraModel, ObEstraUpdateModel } from '../models/ob-es.model';

@Component({
  selector: 'app-ob-es-form',
  templateUrl: './ob-es-form.component.html',
})
export class ObEsFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  pnlOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Objetivo Estratégico',
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
      options: this.pnlOptions
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
    private obEsService: ObEstraService,
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
    this.loadPnlOptions();
    
    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadObEs(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadPnlOptions(): void {
    this.metaService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const pnlList = response.content || [];
        this.pnlOptions = pnlList.map((pnl: any) => ({
          label: pnl.codigo + ' / ' + pnl.descripcion,
          value: pnl.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoMetaFk') {
            return {...field, options: this.pnlOptions};
          }
          return field;
        });
      },
      error: (err) => {
        console.error('Error al cargar Programas Nacionales:', err);
        this.showError('Error al cargar lista de Programas Nacionales');
      }
    });
  }

  loadObEs(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.obEsService.getById(codigo).subscribe({
      next: (obEs) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = obEs[field.name as keyof typeof obEs];
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
        console.error('Error al cargar Objetivo Estratégico:', err);
        this.showError('Error al cargar el Objetivo Estratégico');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: ObEstraUpdateModel): void {
    if (!formValue.descripcion || !formValue.codigoMetaFk) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const obEsData: ObEstraUpdateModel = {
      codigoMetaFk: formValue.codigoMetaFk,
      descripcion: formValue.descripcion,
      estado: 'A'
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Objetivo Estratégico?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.obEsService.update(this.codigo, obEsData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Objetivo Estratégico actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Objetivo Estratégico: ${err.message}`);
            }
          });
        } else {
          const createData: ObEstraModel = {
            ...obEsData,
            codigo: formValue.codigo,
            estado: 'A'
          };
          this.obEsService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Objetivo Estratégico creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Objetivo Estratégico: ${err.message}`);
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