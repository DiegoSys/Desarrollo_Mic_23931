import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormField } from '../../../../../../shared/components/generic-form/generic-form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { SubGrupoService } from '../subGrupo.service';
import { GrupoService } from '../../grupo/grupo.service';
import { ItemService } from './../../item/item.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { SubGrupoModel, SubGrupoUpdateModel } from '../models/subGrupo.model';

@Component({
  selector: 'app-subgrupo-form',
  templateUrl: './subGrupo-form.component.html',
})
export class SubGrupoFormComponent implements OnInit {
  @Input() codigoGrupo?: string;

  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;

  grupoOptions: any[] = [];
  itemOptions: any[] = [];

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
        Validators.maxLength(1000)
      ]
    },
    {
      name: 'codigoGrupoFk',
      label: 'Grupo',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.grupoOptions,
      optionLabel: 'nombre',
      optionValue: 'codigo',
      validators: [Validators.required],
      disabled: !!this.codigoGrupo
    }
  ];

  constructor(
    private fb: FormBuilder,
    private subGrupoService: SubGrupoService,
    private grupoService: GrupoService,
    private itemService: ItemService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService,

  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadOptions();

    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      this.codigoGrupo = params['codigoGrupo'] || this.codigoGrupo;

      // Si NO está editando, setea el valor por defecto del campo 'codigoGrupoFk'
      if (!this.isEditing && this.codigoGrupo) {
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoGrupoFk') {
            return { ...field, defaultValue: this.codigoGrupo, disabled: true };
          }
          return field;
        });
      }

      if (this.isEditing) {
        this.loadSubGrupo(this.codigo);
      } else {
        this.spinnerService.hide();
      }
      
    });
  }

  loadOptions(): void {
    this.spinnerService.show();

    this.grupoService.getAll({ page: 0, size: 100000 }).subscribe({
      next: (response) => {
        this.grupoOptions = response.content || [];
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoGrupoFk') {
            return { ...field, options: this.grupoOptions };
          }
          return field;
        });
      },
      error: () => {
        this.showError('Error al cargar los grupos');
      }
    });

    this.itemService.getAll({ page: 0, size: 100000 }).subscribe({
      next: (response) => {
        this.itemOptions = response.content || [];
        this.formFields = this.formFields.map(field => {
          if (field.name === 'presItemList') {
            return { ...field, options: this.itemOptions };
          }
          return field;
        });
        this.spinnerService.hide();
      },
      error: () => {
        this.showError('Error al cargar los items');
        this.spinnerService.hide();
      }
    });
  }

  loadSubGrupo(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.subGrupoService.getById(codigo).subscribe({
      next: (subGrupo) => {
        this.codigoOriginal = subGrupo.codigo;
        this.formFields = this.formFields.map(field => {
          let defaultValue = subGrupo[field.name as keyof typeof subGrupo];
          // Para el multiselect, asegúrate de que sea un array de códigos
          if (field.name === 'presItemList' && Array.isArray(defaultValue)) {
            defaultValue = defaultValue.map((g: any) => typeof g === 'string' ? g : g.codigo);
          }
          // Deshabilita el campo de grupo al editar
          if (field.name === 'codigoGrupoFk') {
            return {
              ...field,
              defaultValue: defaultValue,
              disabled: true
            };
          }
          // El campo código también debe estar deshabilitado al editar
          if (field.name === 'codigo') {
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
        this.showError('Error al cargar el SubGrupo');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: SubGrupoUpdateModel): void {
    if (!formValue.nombre || !formValue.descripcion || !formValue.codigo || !formValue.codigoGrupoFk) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }

    // Solo un código de grupo y varios items (solo códigos)
    const presItemList = (formValue.presItemList || []).map((g: any) => typeof g === 'string' ? g : g.codigo);

    // Construye el código compuesto solo si está creando
    let codigoCompuesto = formValue.codigo;
    if (!this.isEditing && this.codigoGrupo) {
      codigoCompuesto = `${this.codigoGrupo}.${formValue.codigo}`;
    }

    const subGrupoData: SubGrupoUpdateModel = {
      ...formValue,
      codigo: codigoCompuesto,
      codigoGrupoFk: formValue.codigoGrupoFk,
      presItemList: presItemList
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este SubGrupo?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          const codigoPath = this.codigoOriginal || this.codigo;
          this.subGrupoService.update(codigoPath, subGrupoData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'SubGrupo actualizado correctamente'
              });

              //regresar con params a la lista de grupos
              this.router.navigate(['/lineamiento/operativo/clasif-presup/grupo/view'], {
                queryParams: {
                  codigo: this.codigoGrupo,
                  codigoNaturaleza: this.route.snapshot.queryParams['codigoNaturaleza']
                }
              });

            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar el SubGrupo: ${err.message}`);
            }
          });
        } else {
          const createData: SubGrupoModel = {
            ...subGrupoData,
            codigo: codigoCompuesto,
            presItemList: presItemList
          };
          this.subGrupoService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'SubGrupo creado correctamente'
              });

              //regresar con params a la lista de grupos
              this.router.navigate(['/lineamiento/operativo/clasif-presup/grupo/view'], {
                queryParams: {
                  codigo: this.codigoGrupo,
                  codigoNaturaleza: this.route.snapshot.queryParams['codigoNaturaleza']
                }
              });


            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear el SubGrupo: ${err.message}`);
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
    //regresar con params a la lista de grupos
    this.router.navigate(['/lineamiento/operativo/clasif-presup/grupo/view'], {
      queryParams: {
        codigo: this.codigoGrupo,
        codigoNaturaleza: this.route.snapshot.queryParams['codigoNaturaleza']
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