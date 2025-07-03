import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ItemService } from '../item.service';
import { SubGrupoService } from '../../subGrupo/subGrupo.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ItemModel, ItemUpdateModel } from '../models/item.model';
import { Location } from '@angular/common';

@Component({
  selector: 'app-item-form',
  templateUrl: './item-form.component.html',
})
export class ItemFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  subGrupoOptions: any[] = [];

  // Guarda el código original para updates
  codigoOriginal: string | undefined;

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [
        Validators.required,
        Validators.maxLength(15),
        Validators.pattern('^[a-zA-Z0-9._-]+$')
      ],
      disabled: false
    },
    {
      name: 'nombre',
      label: 'Nombre',
      type: 'text' as const,
      required: true,
      width: 'half',
      validators: [Validators.required, Validators.maxLength(100)]
    },
    {
      name: 'descripcion',
      label: 'Descripción',
      type: 'textarea' as const,
      required: true,
      width: 'full',
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(500)
      ]
    },
    {
      name: 'codigoSubGrupoFk',
      label: 'Subgrupo',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.subGrupoOptions,
      optionLabel: 'nombre',
      optionValue: 'codigo',
      validators: [Validators.required]
    }
  ];

  constructor(
    private fb: FormBuilder,
    private itemService: ItemService,
    private subGrupoService: SubGrupoService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService,
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadSubGrupoOptions();

    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      const codigoSubGrupo = params['codigoSubGrupo'];
      this.isEditing = !!this.codigo;

      // Si NO está editando y viene el código de subgrupo por el path, setea el valor por defecto y deshabilita el campo
      if (!this.isEditing && codigoSubGrupo) {
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoSubGrupoFk') {
            return { ...field, defaultValue: codigoSubGrupo, disabled: true };
          }
          return field;
        });
      }

      // Si está editando, deshabilita el campo de subgrupo también
      if (this.isEditing) {
        this.loadItem(this.codigo);
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoSubGrupoFk') {
            return { ...field, disabled: true };
          }
          return field;
        });
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadSubGrupoOptions(): void {
    this.subGrupoService.getAll({ page: 0, size: 100000 }).subscribe({
      next: (response) => {
        this.subGrupoOptions = response.content || [];
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoSubGrupoFk') {
            return { ...field, options: this.subGrupoOptions };
          }
          return field;
        });
      },
      error: () => {
        this.showError('Error al cargar los subgrupos');
      }
    });
  }

  loadItem(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.itemService.getById(codigo).subscribe({
      next: (item) => {
        this.codigoOriginal = item.codigo;
        this.formFields = this.formFields.map(field => {
          let defaultValue = item[field.name as keyof typeof item];
          // El campo código también debe estar deshabilitado al editar
          if (field.name === 'codigo') {
            return {
              ...field,
              defaultValue: defaultValue,
              disabled: true
            };
          }
          // El campo subgrupo también debe estar deshabilitado al editar
          if (field.name === 'codigoSubGrupoFk') {
            return {
              ...field,
              defaultValue: defaultValue,
              disabled: true
            };
          }
          return {
            ...field,
            defaultValue: defaultValue
          };
        });
        this.loading = false;
        this.spinnerService.hide();
      },
      error: () => {
        this.showError('Error al cargar el Item');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: ItemUpdateModel): void {
    if (!formValue.nombre || !formValue.descripcion || !formValue.codigo || !formValue.codigoSubGrupoFk) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }

    // Si NO está editando y viene el código de subgrupo, concatena el código
    let codigoCompuesto = formValue.codigo;
    const codigoSubGrupo = this.route.snapshot.queryParamMap.get('codigoSubGrupo');
    if (!this.isEditing && codigoSubGrupo) {
      codigoCompuesto = `${codigoSubGrupo}.${formValue.codigo}`;
    }

    const itemData: ItemUpdateModel = {
      ...formValue,
      codigo: codigoCompuesto,
      codigoSubGrupoFk: formValue.codigoSubGrupoFk
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Item?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          const codigoPath = this.codigoOriginal || this.codigo;
          this.itemService.update(codigoPath, itemData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Item actualizado correctamente'
              });
              const codigoSubGrupo = this.route.snapshot.queryParamMap.get('codigoSubGrupo');
              const codigoGrupo = this.route.snapshot.queryParamMap.get('codigoGrupo');
              const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
              this.router.navigate(['/lineamiento/operativo/clasif-presup/subGrupo/view'], {
                queryParams: {
                  codigo: codigoSubGrupo,
                  codigoGrupo: codigoGrupo,
                  codigoNaturaleza: codigoNaturaleza
                }
              });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el Item: ${err.message}`);
            }
          });
        } else {
          const createData: ItemModel = {
            ...itemData,
            codigo: codigoCompuesto,
            estado: 'A'
          };
          this.itemService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Item creado correctamente'
              });
              const codigoSubGrupo = this.route.snapshot.queryParamMap.get('codigoSubGrupo');
              const codigoGrupo = this.route.snapshot.queryParamMap.get('codigoGrupo');
              const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
              this.router.navigate(['/lineamiento/operativo/clasif-presup/subGrupo/view'], {
                queryParams: {
                  codigo: codigoSubGrupo,
                  codigoGrupo: codigoGrupo,
                  codigoNaturaleza: codigoNaturaleza
                }
              });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el Item: ${err.message}`);
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
    const codigoSubGrupo = this.route.snapshot.queryParamMap.get('codigoSubGrupo');
    const codigoGrupo = this.route.snapshot.queryParamMap.get('codigoGrupo');
    const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
    this.router.navigate(['/lineamiento/operativo/clasif-presup/subGrupo/view'], {
      queryParams: {
        codigo: codigoSubGrupo,
        codigoGrupo: codigoGrupo,
        codigoNaturaleza: codigoNaturaleza
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