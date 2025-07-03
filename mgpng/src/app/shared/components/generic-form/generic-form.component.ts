/**
 * Componente de formulario genérico reutilizable.
 * Permite renderizar formularios dinámicos a partir de un arreglo de campos (fields).
 * Soporta validaciones, campos de distintos tipos, integración con PrimeNG y navegación para añadir opciones.
 */

import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

/**
 * Interfaz para definir los campos del formulario.
 */
export interface FormField {
  name: string; // Nombre del control
  label: string; // Etiqueta a mostrar
  type: 'number' | 'text' | 'date' | 'textarea' | 'select' | 'checkbox' | 'multiselect' | 'daterange'; // Tipo de campo
  required?: boolean; // Si es obligatorio
  options?: any[]; // Opciones para select/multiselect
  optionLabel?: string; // Propiedad para mostrar en opciones
  optionValue?: string; // Propiedad para el valor en opciones
  defaultValue?: any; // Valor por defecto
  validators?: any[]; // Validadores adicionales
  width?: 'full' | 'half' | 'third' | 'quarter'; // Ancho visual
  cssClass?: string; // Clases CSS adicionales
  disabled?: boolean; // Si está deshabilitado
  onChange?: (value: any, formValue: any) => void; // Función a ejecutar al cambiar el valor
  prefix?: string; // Prefijo visual para inputs
  maxLength?: number | null; // Longitud máxima (calculada)
  showAddButton?: boolean; // Mostrar botón para añadir opciones
  pattern?: string; // <-- Permitir definir un patrón personalizado (como permitir puntos)

}

@Component({
  selector: 'app-generic-form',
  templateUrl: './generic-form.component.html',
  styleUrls: ['./generic-form.component.scss']
})
export class GenericFormComponent implements OnChanges {
  /**
   * Título del formulario.
   */
  @Input() title: string = '';
  /**
   * Definición de los campos del formulario.
   */
  @Input() fields: FormField[] = [];
  /**
   * Texto del botón de submit.
   */
  @Input() submitText: string = 'Guardar';
  /**
   * Mostrar o no el botón de cancelar.
   */
  @Input() showCancelButton: boolean = false;
  /**
   * Evento emitido al enviar el formulario.
   */
  @Output() formSubmit = new EventEmitter<any>();
  /**
   * Evento emitido al cancelar.
   */
  @Output() cancel = new EventEmitter<void>();
  /**
   * Indica si el formulario está en modo edición.
   */
  @Input() isEditing: boolean = false;

  /**
   * Indica si se debe mostrar el campo 'codigoPadre'.
   * Se usa para formularios que dependen de un código padre.
   */
  @Input() showCodigoCompuesto: boolean = false;

  /**
   * FormGroup reactivo.
   */
  form: FormGroup;

  /**
   * Evento emitido al añadir una opción en selects.
   * Se usa para redirigir a un formulario de creación de opciones.
   */

  @Output() addOption = new EventEmitter<FormField>();

  constructor(private fb: FormBuilder, private router: Router) {
    this.form = this.fb.group({});
  }

  /**
   * Se ejecuta cuando cambian los @Input.
   * Calcula maxLength y crea el formulario.
   */
  ngOnChanges(changes: SimpleChanges) {
    if (changes['fields']) {
      // Agrega maxLength a cada campo si tiene el validador
      this.fields = this.fields.map(field => {
        let maxLength = null;
        if (field.validators) {
          const maxLengthValidator = field.validators.find((v: any) => v?.maxLength);
          if (maxLengthValidator) {
            maxLength = maxLengthValidator.maxLength;
          }
        }
        return {
          ...field,
          maxLength
        };
      });
      this.createForm();
    }
  }

  /**
   * Crea el FormGroup y aplica validadores.
   * También suscribe a cambios si el campo tiene onChange.
   */
  createForm() {
    const controls = {};
    this.fields.forEach(field => {
      const validators = field.required ? [Validators.required] : [];
      if (field.validators) {
        validators.push(...field.validators);
      }
      controls[field.name] = [
        { 
          value: field.defaultValue || (field.type === 'multiselect' ? [] : ''), 
          disabled: field.disabled || false 
        },
        validators
      ];
    });
    this.form = this.fb.group(controls);

    // Setup change handlers for fields with onChange
    this.fields.forEach(field => {
      if (field.onChange) {
        this.form.get(field.name)?.valueChanges.subscribe(value => {
          field.onChange?.(value, this.form.getRawValue());
        });
      }
    });
  }

  /**
   * Acción al pulsar el botón "+" en selects.
   * Redirige a un formulario para añadir opciones.
   * Personaliza las rutas según el campo.
   */
  onAddOption(field: FormField) {
    this.addOption.emit(field);
  }

  /**
   * Obtiene el valor del campo 'codigoPadre' (el primer campo que no sea 'codigo' y termine en 'Fk').
   * Si no existe, retorna undefined.
   */
  getCodigoPadre(): string | undefined {
    // Busca el primer campo que no sea 'codigo' y termine en 'Fk'
    const padreField = this.fields.find(f =>
      f.name !== 'codigo' &&
      f.name.endsWith('Fk') &&
      this.form.get(f.name)
    );
    return padreField ? this.form.get(padreField.name)?.value : undefined;
  }

  /**
   * Acción al enviar el formulario.
   * Valida campos requeridos y emite el evento.
   */
  onSubmit() {
    if (this.form.valid) {
      const formData = this.form.getRawValue();
      // Verificar que los campos requeridos tienen valores
      const missingFields = this.fields
        .filter(field => field.required && (
          formData[field.name] === undefined ||
          formData[field.name] === null ||
          (Array.isArray(formData[field.name]) && formData[field.name].length === 0) ||
          formData[field.name] === ''
        ))
        .map(field => field.label);

      if (missingFields.length > 0) {
        console.error('Missing required fields:', missingFields);
        return;
      }

      this.formSubmit.emit(formData);
    } else {
      this.form.markAllAsTouched();
    }
  }
}
