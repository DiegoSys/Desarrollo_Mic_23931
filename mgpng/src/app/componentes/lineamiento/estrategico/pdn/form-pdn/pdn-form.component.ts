import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PdnService } from '../pdn.service';
import { OpnService } from '../../opn/opn.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { PdnModel,PdnUpdateModel } from '../models/pdn.model';


@Component({
  selector: 'app-pdn-form',
  templateUrl: './pdn-form.component.html',
})
export class PdnFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  pnOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Política PDN',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [
        Validators.required,
        Validators.maxLength(15),
        Validators.pattern('^[a-zA-Z0-9._-]+$') //Permite letras, números, puntos, guiones y guiones bajos
      ],
      disabled: this.isEditing,

    },
    {
      name: 'codigoOpnFk',
      label: 'Objetivo Nacional Relacionado',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.pnOptions,
      showAddButton: true,
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
    private pdnService: PdnService,
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
    this.loadPnOptions();
    
    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadPdn(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadPnOptions(): void {
    this.opnService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const pnList = response.content || [];
        this.pnOptions = pnList.map((pn: PdnModel) => ({
          label: (pn.codigo + ' / ' + pn.descripcion).length > 80
            ? (pn.codigo + ' / ' + pn.descripcion).substring(0, 77) + '...'
            : pn.codigo + ' / ' + pn.descripcion,
          value: pn.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoOpnFk') {
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

  loadPdn(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.pdnService.getById(codigo).subscribe({
      next: (pdn) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = pdn[field.name as keyof typeof pdn];
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
        console.error('Error al cargar Política PDN:', err);
        this.showError('Error al cargar la Política del PDN');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: PdnModel): void {
    if (!formValue.descripcion || !formValue.codigoOpnFk ) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }
    
    const pdnData: PdnUpdateModel = {
      codigoOpnFk: formValue.codigoOpnFk,
      descripcion: formValue.descripcion,
      estado: 'A', // Default active status
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} esta Política del PDN?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.pdnService.update(this.codigo, pdnData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Política del PDN actualizada correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar la Política del PDN: ${err.message}`);
            }
          });
        } else {
          const createData: PdnModel = {
            ...pdnData,
            codigo: formValue.codigo
          };
          this.pdnService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Política del PDN creada correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear la Política del PDN: ${err.message}`);
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
    if (field.name === 'codigoOpnFk') {
      // Redirige al formulario de creación de ODS (ajusta la ruta según tu app)
      this.router.navigate(['/lineamiento/opn/new']);
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