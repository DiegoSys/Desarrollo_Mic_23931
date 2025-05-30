import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

export interface FormField {
  name: string;
  label: string;
  type: 'number' | 'text' | 'date' | 'textarea' | 'select' | 'checkbox' | 'multiselect';
  required?: boolean;
  options?: any[];
  optionLabel?: string;
  optionValue?: string;
  defaultValue?: any;
  validators?: any[];
  width?: 'full' | 'half' | 'third' | 'quarter';
  cssClass?: string;
  disabled?: boolean;
  onChange?: (value: any, formValue: any) => void;
  prefix?: string;
  maxLength?: number | null;
}

@Component({
  selector: 'app-generic-form',
  templateUrl: './generic-form.component.html',
  styleUrls: ['./generic-form.component.scss']
})
export class GenericFormComponent implements OnChanges {
  @Input() title: string = '';
  @Input() fields: FormField[] = [];
  @Input() submitText: string = 'Guardar';
  @Input() showCancelButton: boolean = false;
  @Output() formSubmit = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();
  @Input() isEditing: boolean = false;


  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({});
  }

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
