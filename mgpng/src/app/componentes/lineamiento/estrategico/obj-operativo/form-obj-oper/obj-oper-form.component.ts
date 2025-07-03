import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ObjOperService } from '../obj-oper.service';
import { ObEstraService } from '../../obj-estrategico/ob-es.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ObjOperModel, ObjOperUpdateModel } from '../models/obj-oper.model';

@Component({
  selector: 'app-obj-oper-form',
  templateUrl: './obj-oper-form.component.html',
})
export class ObjOperFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  obEsOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Objetivo Operativo',
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
      name: 'codigoEstrFk',
      label: 'Objetivo Estratégico Relacionado',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.obEsOptions,
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
        Validators.maxLength(1000)
      ]
    }
  ];

  constructor(
    private fb: FormBuilder,
    private objOperService: ObjOperService,
    private obEsService: ObEstraService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadObEsOptions();
    
    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadObjOper(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadObEsOptions(): void {
    this.obEsService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const obEsList = response.content || [];
        this.obEsOptions = obEsList.map((obEs: any) => ({
          label: (obEs.codigo + ' / ' + obEs.descripcion).length > 80
            ? (obEs.codigo + ' / ' + obEs.descripcion).substring(0, 77) + '...'
            : obEs.codigo + ' / ' + obEs.descripcion,
          value: obEs.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoEstrFk') {
            return {...field, options: this.obEsOptions};
          }
          return field;
        });
      },
      error: (err) => {
        console.error('Error al cargar Objetivos Estratégicos:', err);
        this.showError('Error al cargar lista de Objetivos Estratégicos');
      }
    });
  }

  loadObjOper(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.objOperService.getById(codigo).subscribe({
      next: (objOper) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = objOper[field.name as keyof typeof objOper];
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
        console.error('Error al cargar Objetivo Operativo:', err);
        this.showError('Error al cargar el Objetivo Operativo');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: ObjOperUpdateModel): void {
    if (!formValue.descripcion || !formValue.codigoEstrFk ) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const objOperData: ObjOperUpdateModel = {
      codigoEstrFk: formValue.codigoEstrFk,
      descripcion: formValue.descripcion,
      estado: 'A'
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Objetivo Operativo?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.objOperService.update(this.codigo, objOperData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Objetivo Operativo actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Objetivo Operativo: ${err.message}`);
            }
          });
        } else {
          const createData: ObjOperModel = {
            ...objOperData,
            codigo: formValue.codigo,
            estado: 'A'
          };
          this.objOperService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Objetivo Operativo creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Objetivo Operativo: ${err.message}`);
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
    if (field.name === 'codigoEstrFk') {
      // Redirige al formulario de creación de ODS (ajusta la ruta según tu app)
      this.router.navigate(['/lineamiento/obj-estra/new']);
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