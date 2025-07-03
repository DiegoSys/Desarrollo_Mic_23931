import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormField } from '../../../../../../shared/components/generic-form/generic-form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { GrupoService } from '../grupo.service';
import { NaturalezaService } from './../../naturaleza/naturaleza.service';
import { SubGrupoService } from '../../subGrupo/subGrupo.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { GrupoModel, GrupoUpdateModel } from '../models/grupo.model';

@Component({
  selector: 'app-grupo-form',
  templateUrl: './grupo-form.component.html',
})
export class GrupoFormComponent implements OnInit {
  @Input() codigoNaturaleza?: string;

  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;

  naturalezaOptions: any[] = [];
  subGrupoOptions: any[] = [];

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
        Validators.pattern('^[a-zA-Z0-9._-]+$') //Permite letras, números, puntos, guiones y guiones bajos
      ],
      disabled: this.isEditing
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
      name: 'codigoNaturalezaFk',
      label: 'Naturaleza',
      type: 'select' as const,
      required: true,
      width: 'half',
      options: this.naturalezaOptions,
      optionLabel: 'nombre',
      optionValue: 'codigo',
      validators: [Validators.required],
      disabled: !!this.codigoNaturaleza
    },
  ];

  constructor(
    private fb: FormBuilder,
    private grupoService: GrupoService,
    private naturalezaService: NaturalezaService,
    private subGrupoService: SubGrupoService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
  this.loadOptions();

  this.route.queryParams.subscribe(params => {
    this.codigo = params['codigo'];
    this.isEditing = !!this.codigo;
    this.codigoNaturaleza = params['codigoNaturaleza'] || this.codigoNaturaleza;

    // Si NO está editando, setea el valor por defecto del campo 'codigoNaturalezaFk'
    if (!this.isEditing && this.codigoNaturaleza) {
      this.formFields = this.formFields.map(field => {
        if (field.name === 'codigoNaturalezaFk') {
          return { ...field, defaultValue: this.codigoNaturaleza, disabled: true };
        }
        return field;
      });
    }

    if (this.isEditing) {
      this.loadGrupo(this.codigo);
    } else {
      this.spinnerService.hide();
    }
  });
}

  loadOptions(): void {
    this.spinnerService.show();

    this.naturalezaService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        this.naturalezaOptions = response.content || [];
        this.formFields = this.formFields.map(field => {
          if (field.name === 'codigoNaturalezaFk') {
            return { ...field, options: this.naturalezaOptions };
          }
          return field;
        });
      },
      error: () => {
        this.showError('Error al cargar las naturalezas');
      }
    });

    this.subGrupoService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        this.subGrupoOptions = response.content || [];
        //console.log('Subgrupos cargados:', this.subGrupoOptions);
        this.formFields = this.formFields.map(field => {
          if (field.name === 'presSubGrupoList') {
            return { ...field, options: this.subGrupoOptions };
          }
          return field;
        });
        this.spinnerService.hide();
      },
      error: () => {
        this.showError('Error al cargar los subgrupos');
        this.spinnerService.hide();
      }
    });
  }

  loadGrupo(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.grupoService.getById(codigo).subscribe({
      next: (grupo) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = grupo[field.name as keyof typeof grupo];
          // Para el multiselect, asegúrate de que sea un array de códigos
          if (field.name === 'presSubGrupoList' && Array.isArray(defaultValue)) {
            defaultValue = defaultValue.map((g: any) => typeof g === 'string' ? g : g.codigo);
          }
          // Deshabilita el campo de naturaleza al editar
          if (field.name === 'codigoNaturalezaFk') {
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
        this.showError('Error al cargar el Grupo');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: GrupoUpdateModel): void {
  if (!formValue.nombre || !formValue.descripcion || !formValue.codigo || !formValue.codigoNaturalezaFk) {
    this.showError('Faltan datos requeridos en el formulario');
    return;
  }

  // Solo un código de naturaleza y varios subgrupos (solo códigos)
  const presSubGrupoList = (formValue.presSubGrupoList || []).map((g: any) => typeof g === 'string' ? g : g.codigo);

  // Construye el código compuesto solo si está creando
  let codigoCompuesto = formValue.codigo;
  if (!this.isEditing && this.codigoNaturaleza) {
    codigoCompuesto = `${this.codigoNaturaleza}.${formValue.codigo}`;
  }

  const grupoData: GrupoUpdateModel = {
    ...formValue,
    codigo: codigoCompuesto,
    codigoNaturalezaFk: formValue.codigoNaturalezaFk,
    presSubGrupoList: presSubGrupoList
  };

  this.confirmationService.confirm({
    message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} este Grupo?`,
    header: 'Confirmación',
    icon: 'pi pi-question-circle',
    accept: () => {
      this.spinnerService.show();
      if (this.isEditing) {
        this.grupoService.update(this.codigo, grupoData).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Éxito',
              detail: 'Grupo actualizado correctamente'
            });
            const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
            this.router.navigate(
              ['/lineamiento/operativo/clasif-presup/naturaleza/view'],
              { queryParams: { codigo: codigoNaturaleza } }
            );
          },
          error: (err) => {
            this.spinnerService.hide();
            this.showError(`Error al actualizar el Grupo: ${err.message}`);
          }
        });
      } else {
        const createData: GrupoModel = {
          ...grupoData,
          codigo: codigoCompuesto,
          estado: 'A',
          presSubGrupoList: presSubGrupoList
        };
        this.grupoService.create(createData).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Éxito',
              detail: 'Grupo creado correctamente'
            });
            const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
            this.router.navigate(
              ['/lineamiento/operativo/clasif-presup/naturaleza/view'],
              { queryParams: { codigo: codigoNaturaleza } }
            );
          },
          error: (err) => {
            this.spinnerService.hide();
            this.showError(`Error al crear el Grupo: ${err.message}`);
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
    // Obtiene el código de naturaleza de los query params y navega a la vista de naturaleza
    const codigoNaturaleza = this.route.snapshot.queryParamMap.get('codigoNaturaleza');
    this.router.navigate(
      ['/lineamiento/operativo/clasif-presup/naturaleza/view'],
      { queryParams: { codigo: codigoNaturaleza } }
    );
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