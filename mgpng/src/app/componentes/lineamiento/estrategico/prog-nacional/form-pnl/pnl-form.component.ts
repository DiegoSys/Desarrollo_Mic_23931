import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PnlService } from '../pnl.service';
import { MetaService } from '../../meta/meta.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { PnlModel, PnlUpdateModel } from '../models/pnl.model';

@Component({
  selector: 'app-pnl-form',
  templateUrl: './pnl-form.component.html',
})
export class PnlFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  metaOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Programa Nacional',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [
        Validators.required, 
        Validators.maxLength(15),
        Validators.pattern('^[a-zA-Z0-9._-]+$') // Permite letras, números, puntos, guiones y guiones bajos
      ],
      disabled: this.isEditing
    },
    {
      name: 'codigoMetaFk',
      label: 'Meta Relacionada',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.metaOptions,
      showAddButton: true
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
    private pnlService: PnlService,
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
        this.loadPnl(this.codigo);
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
          label: (meta.codigo + ' / ' + meta.descripcion).length > 80
            ? (meta.codigo + ' / ' + meta.descripcion).substring(0, 77) + '...'
            : meta.codigo + ' / ' + meta.descripcion,
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

  loadPnl(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.pnlService.getById(codigo).subscribe({
      next: (pnl) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = pnl[field.name as keyof typeof pnl];
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
        console.error('Error al cargar Programa Nacional:', err);
        this.showError('Error al cargar el Programa Nacional');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: PnlUpdateModel): void {
    if (!formValue.descripcion || !formValue.codigoMetaFk) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const pnlData: PnlUpdateModel = {
      codigoMetaFk: formValue.codigoMetaFk,
      descripcion: formValue.descripcion,
      estado: 'A' // Ajusta el valor por defecto según tu lógica de negocio
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Programa Nacional?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.pnlService.update(this.codigo, pnlData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Programa Nacional actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Programa Nacional: ${err.message}`);
            }
          });
        } else {
          const createData: PnlModel = {
            ...pnlData,
            codigo: formValue.codigo,
            estado: 'A' // Ajusta el valor por defecto según tu lógica de negocio
          };
          this.pnlService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Programa Nacional creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Programa Nacional: ${err.message}`);
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

  /**
   * Método para el botón "+" de añadir opción.
   * Redirige al formulario de creación de ODS si se presiona en el campo de ODS.
   */
  onAddOption(field: FormField) {
    if (field.name === 'codigoMetaFk') {
      // Redirige al formulario de creación de ODS (ajusta la ruta según tu app)
      this.router.navigate(['/lineamiento/meta/new']);
    }
    // Puedes agregar más casos para otros campos si lo necesitas
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