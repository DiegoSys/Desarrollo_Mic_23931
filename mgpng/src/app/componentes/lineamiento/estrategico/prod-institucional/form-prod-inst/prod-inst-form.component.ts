import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProdInsService } from '../prod-inst.service';
import { ProInsService } from '../../prog-institucional/prog-ins.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ProdInsModel, ProdInsUpdateModel } from '../models/prod-inst.model';

@Component({
  selector: 'app-prod-inst-form',
  templateUrl: './prod-inst-form.component.html',
})
export class ProdInstFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  progInsOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Producto Institucional',
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
      name: 'codigoProginstFk',
      label: 'Programa Institucional Relacionado',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.progInsOptions,
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
    private prodInsService: ProdInsService,
    private progInsService: ProInsService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadProgInsOptions();
    
    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadProdIns(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadProgInsOptions(): void {
    this.progInsService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const progInsList = response.content || [];
        this.progInsOptions = progInsList.map((progIns: any) => ({
          label: (progIns.codigo + ' / ' + progIns.descripcion).length > 80
            ? (progIns.codigo + ' / ' + progIns.descripcion).substring(0, 77) + '...'
            : progIns.codigo + ' / ' + progIns.descripcion,
          value: progIns.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoProginstFk') {
            return {...field, options: this.progInsOptions};
          }
          return field;
        });
      },
      error: (err) => {
        console.error('Error al cargar Programas Institucionales:', err);
        this.showError('Error al cargar lista de Programas Institucionales');
      }
    });
  }

  loadProdIns(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.prodInsService.getById(codigo).subscribe({
      next: (prodIns) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = prodIns[field.name as keyof typeof prodIns];
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
        console.error('Error al cargar Producto Institucional:', err);
        this.showError('Error al cargar el Producto Institucional');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: ProdInsUpdateModel): void {
    if (!formValue.descripcion || !formValue.codigoProginstFk ) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const prodInsData: ProdInsUpdateModel = {
      codigoProginstFk: formValue.codigoProginstFk,
      descripcion: formValue.descripcion,
      estado: 'A'
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Producto Institucional?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.prodInsService.update(this.codigo, prodInsData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Producto Institucional actualizado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Producto Institucional: ${err.message}`);
            }
          });
        } else {
          const createData: ProdInsModel = {
            ...prodInsData,
            codigo: formValue.codigo,
            estado: 'A'
          };
          this.prodInsService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Producto Institucional creado correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Producto Institucional: ${err.message}`);
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
    if (field.name === 'codigoProginstFk') {
      // Redirige al formulario de creación de ODS (ajusta la ruta según tu app)
      this.router.navigate(['/lineamiento/prog-inst/new']);
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