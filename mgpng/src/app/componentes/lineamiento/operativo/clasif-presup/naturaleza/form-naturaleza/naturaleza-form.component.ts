import { Component, OnInit } from '@angular/core';
import { FormField } from 'src/app/shared/components/generic-form/generic-form.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NaturalezaService } from '../naturaleza.service';
import { GrupoService } from './../../grupo/grupo.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { NaturalezaUpdateModel, NaturalezaModel } from '../models/naturaleza.model';

@Component({
  selector: 'app-naturaleza-form',
  templateUrl: './naturaleza-form.component.html',
})
export class NaturalezaFormComponent implements OnInit {
  codigo: string;
  form: FormGroup;
  isEditing = false;
  loading = false;
  grupoOptions: {label: string, value: string}[] = [];

  formFields: FormField[] = [
    {
      name: 'codigo',
      label: 'Código Naturaleza',
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
  ];

  constructor(
    private fb: FormBuilder,
    private naturalezaService: NaturalezaService,
    private grupoService: GrupoService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadGrupoOptions();
    
    this.route.queryParams.subscribe(params => {
      this.codigo = params['codigo'];
      this.isEditing = !!this.codigo;
      if (this.isEditing) {
        this.loadNaturaleza(this.codigo);
      } else {
        this.spinnerService.hide();
      }
    });
  }

  loadGrupoOptions(): void {
    this.grupoService.getAll({page: 0, size: 100000}).subscribe({
      next: (response) => {
        const grupoList = response.content || [];
        // El value debe ser el código del grupo, no el objeto completo
        this.grupoOptions = grupoList.map((grupo: any) => ({
          label: (grupo.codigo + ' / ' + grupo.nombre).length > 80
            ? (grupo.codigo + ' / ' + grupo.nombre).substring(0, 77) + '...'
            : grupo.codigo + ' / ' + grupo.nombre,
          value: grupo.codigo
        }));
        this.formFields = this.formFields.map(field => {
          if (field.name === 'presGrupoList') {
            return {...field, options: this.grupoOptions};
          }
          return field;
        });
      },
      error: (err) => {
        console.error('Error al cargar Grupos:', err);
        this.showError('Error al cargar lista de Grupos');
      }
    });
  }

  loadNaturaleza(codigo: string): void {
    this.loading = true;
    this.spinnerService.show();
    this.naturalezaService.getById(codigo).subscribe({
      next: (naturaleza) => {
        this.formFields = this.formFields.map(field => {
          let defaultValue = naturaleza[field.name as keyof typeof naturaleza];
          // Si es presGrupoList, asegúrate de que sea un array de códigos
          if (field.name === 'presGrupoList' && Array.isArray(defaultValue)) {
            defaultValue = defaultValue.map((g: any) => typeof g === 'string' ? g : g.codigo);
          }
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
        this.showError('Error al cargar la Naturaleza');
        this.spinnerService.hide();
      }
    });
  }

  onSubmit(formValue: NaturalezaUpdateModel): void {
    if (!formValue.nombre || !formValue.descripcion) {
      this.showError('Faltan datos requeridos en el formulario');
      return;
    }

    // Asegúrate de enviar solo los códigos de grupo
    const presGrupoList = (formValue.presGrupoList || []).map((g: any) => typeof g === 'string' ? g : g.codigo);

    const naturalezaData: NaturalezaUpdateModel = {
      nombre: formValue.nombre,
      descripcion: formValue.descripcion,
      presGrupoList: presGrupoList
    };

    this.confirmationService.confirm({
      message: `¿Está seguro de que desea ${this.isEditing ? 'actualizar' : 'crear'} esta Naturaleza?`,
      header: 'Confirmación',
      icon: 'pi pi-question-circle',
      accept: () => {
        this.spinnerService.show();
        if (this.isEditing) {
          this.naturalezaService.update(this.codigo, naturalezaData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Naturaleza actualizada correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al actualizar la Naturaleza: ${err.message}`);
            }
          });
        } else {
          const createData: NaturalezaModel = {
            ...naturalezaData,
            codigo: formValue.codigo,
            estado: 'A',
            presGrupoList: presGrupoList
          };
          this.naturalezaService.create(createData).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Naturaleza creada correctamente'
              });
              this.router.navigate(['../'], { relativeTo: this.route });
            },
            error: (err) => {
              this.spinnerService.hide();
              this.showError(`Error al crear la Naturaleza: ${err.message}`);
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
    if (field.name === 'presGrupoList') {
      // Redirige al formulario de creación de ODS (ajusta la ruta según tu app)
      this.router.navigate(['/lineamiento/grupo/new']);
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